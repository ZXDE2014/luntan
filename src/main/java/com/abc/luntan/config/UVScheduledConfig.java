package com.abc.luntan.config;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.abc.luntan.entity.Uvcount;
import com.abc.luntan.service.UvcountService;
import com.abc.luntan.utils.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Configuration: 定时任务，系统访问量
 */
@Slf4j
@Configuration
@EnableScheduling
public class UVScheduledConfig {

    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private UvcountService uvcountService;


    //添加定时任务  每天的23点  新增第二天的redis的key值
    @Scheduled(cron = "0 0 23 * * ?")
    private void flashRedisKey(){

        RLock rlock = redissonClient.getLock(RedisConstants.SYSTEM_DAY_VISIT_LOCK);



        String tomorrow = DateUtil.format(DateUtil.tomorrow(), "yyyy-MM-dd");
        String key=RedisConstants.SYSTEM_DAY_VISIT+ tomorrow;

        try {
            boolean lock = rlock.tryLock(10, TimeUnit.SECONDS);
            if(lock){
                log.info("加锁成功"+Thread.currentThread().getName());
                redissonClient.getBucket(key).set(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Error while redisson lock is interrupted");
        }finally {
            rlock.unlock();
            log.info("解锁成功"+Thread.currentThread().getName());
        }

    }


    // 利用可重入分布式锁 解决了分布式环境下，系统访问量不一致的情况，减少了redis的访问压力
    //添加定时任务  每天的2点  记录前一天的访问量到mysql，并更新mysql的总访问量
    @Scheduled(cron = "0 0 2 * * ?")
    private void updateLastDay(){
        String ys = DateUtil.format(DateUtil.yesterday(), "yyyy-MM-dd");
        String key=RedisConstants.SYSTEM_DAY_VISIT+ ys;

        // 分布式锁
        RLock rlock = redissonClient.getLock(RedisConstants.SYSTEM_ALL_VISIT_LOCK);
        String s="";
        try {
            boolean lock = rlock.tryLock(10, TimeUnit.SECONDS);
            if(lock){
                log.info("加锁成功"+Thread.currentThread().getName());
                s= (String)redissonClient.getBucket(key).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Error while redisson lock is interrupted");
        }finally {
            rlock.unlock();
            log.info("解锁成功"+Thread.currentThread().getName());
        }

        if (StrUtil.isBlank(s)) {
            return;
        }
        int lastDayVisit = Integer.parseInt(s);

        int retry=3;
        Uvcount uvcount = new Uvcount();
        uvcount.setDay(DateUtil.yesterday());
        uvcount.setCount(lastDayVisit);
        while (retry-->0){
            if(uvcountService.save(uvcount)){
                break;
            }
        }

        redissonClient.getBucket(key).delete();
        uvcountService.update().eq("id",1).setSql("count = count +"+lastDayVisit).update();
    }

}
