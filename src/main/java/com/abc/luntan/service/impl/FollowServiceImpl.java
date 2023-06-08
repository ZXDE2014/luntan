package com.abc.luntan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.abc.luntan.Event.Event;
import com.abc.luntan.Event.EventProducer;
import com.abc.luntan.dto.UserDTO;
import com.abc.luntan.utils.UserHolder;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abc.luntan.entity.Follow;
import com.abc.luntan.service.FollowService;
import com.abc.luntan.mapper.FollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.abc.luntan.utils.RabbitConstants.TOPIC_FOLLOW;

/**
 *
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{
    @Autowired
    private EventProducer eventProducer;

    @Override
    public CommonResult isFollow(String userId) {
        return CommonResult.success(isFollowed(userId));
    }

    @Override
    public CommonResult follow(String userId, boolean isFollow) {
        // 判断关注 还是取关
        boolean hasFollowed = isFollowed(userId);
        if(isFollow && !hasFollowed){
            // 想要关注 and 没有关注
            Follow follow = new Follow(null,UserHolder.getUser().getEmail(),userId, DateUtil.date());
            if (save(follow)) {
                sentMq(userId);
                return CommonResult.success(hasFollowed, "关注成功");
            }else{
                return CommonResult.fail("关注失败");
            }
        }else if (!isFollow && hasFollowed){
            QueryChainWrapper<Follow> chainWrapper = query().eq("user_id", UserHolder.getUser().getEmail()).eq("follow_user_id", userId);
            if (remove(chainWrapper)) {
                return CommonResult.success(hasFollowed, "取关成功");
            }else {
                return CommonResult.fail("取关失败");
            }
        }
        return CommonResult.fail("操作失败");
    }

    public void sentMq(String userid) {
        Event event = new Event(TOPIC_FOLLOW, UserHolder.getUser().getEmail(), "user", userid, userid);
        eventProducer.fireEventDirect(event);
    }


    private boolean isFollowed(String userId){
        UserDTO user= UserHolder.getUser();
        Follow follow = query().eq("user_id", user.getEmail()).eq("follow_user_id", userId).one();
        if(follow==null){
            return false;
        }
        return true;
    }
}




