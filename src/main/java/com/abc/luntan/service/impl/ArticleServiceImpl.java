package com.abc.luntan.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.RandomUtil;
import com.abc.luntan.Event.Event;
import com.abc.luntan.Event.EventProducer;
import com.abc.luntan.dto.ArticleDTO;
import com.abc.luntan.dto.UserDTO;
import com.abc.luntan.entity.User;
import com.abc.luntan.service.*;
import com.abc.luntan.utils.BadWordUtil;
import com.abc.luntan.utils.MyFileUtil;
import com.abc.luntan.utils.UserHolder;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abc.luntan.entity.Article;
import com.abc.luntan.mapper.ArticleMapper;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static com.abc.luntan.utils.RabbitConstants.TOPIC_LIKE;
import static com.abc.luntan.utils.RedisConstants.ARTICLE_LIKED_KEY;
import static com.abc.luntan.utils.RedisConstants.SYSTEM_BADWORD;
import static com.abc.luntan.utils.SystemConstants.IMAGE_UPLOAD_DIR;
import static com.abc.luntan.utils.SystemConstants.MAX_PAGE_SIZE;

/**
 *
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService{


    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private UserService userService;
    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private EsArticleService esArticleService;



    //联表查询：有用户信息
    @Override
    public CommonResult queryNewArticle(Integer current, Integer category) {
        QueryWrapper<Article> query = new QueryWrapper<>();

        if (category!=0){
            query.eq("article_category_id",category);
        }
        // 获得文章所有的数目
        int count = this.count(query);
        if(count==0){
            return CommonResult.success(new Page<Article>());
        }

        // 深查询优化：1.获取
        query.orderByDesc("created_time");
        query.last("limit "+Math.min(count-10, (current-1)*10)+", 1");
        query.select("created_time");
        Article one = getOne(query);

        query.clear();

        query.le("created_time",one.getCreatedTime()).orderByDesc("created_time");
        if (category!=0){
            query.eq("article_category_id",category);
        }

        IPage<Article> page = articleMapper.listJoinInfoPages(new Page<>(1, MAX_PAGE_SIZE, false), query);
        // 做了 查询优化，需要重新设置 total 和当前页
        page.setTotal(count);
        page.setCurrent(current);
        page.getRecords().forEach(
                this::isArticleLiked
        );

        return CommonResult.success(page);
    }

    private void isArticleLiked(Article article) {
        String userId = UserHolder.getUser().getEmail();
        String key = ARTICLE_LIKED_KEY + article.getArticleId();
        Double score = redissonClient.getScoredSortedSet(key).getScore(userId);
        article.setLiked(score != null);
    }

    @Override
    public CommonResult queryHotArticle(Integer current, Integer category) {
        QueryWrapper<Article> query = new QueryWrapper<>();
        query.ge("created_time", DateUtil.lastMonth()).orderByDesc("article_like_count").orderByDesc("created");
        if (category!=0){
            query.eq("article_category_id",category);
        }
        IPage<Article> page = articleMapper.listJoinInfoPages(new Page<>(current, MAX_PAGE_SIZE), query);

        // 设置 查询得到的结果是否被点赞，点赞后需要输出
        page.getRecords().forEach(
                this::isArticleLiked
        );
        return CommonResult.success(page);
    }

    // 所有的点赞+关注 这种可以重复调用，可以引入 springcloud 和 nocas 做限流，一分钟以内只有调用一次
    @Override
    @Transactional
    public CommonResult likeArticle(Long articleId) {
        String userId = UserHolder.getUser().getEmail();
        String key = ARTICLE_LIKED_KEY + articleId;

        // ScoredSortedSet 是score<即点赞时间> 来排序的

        Double score = redissonClient.getScoredSortedSet(key).getScore(userId);

        if(score != null){
            // 已经点赞 可以取消
            boolean isSuccess = redissonClient.getScoredSortedSet(key).remove(userId);
            if(isSuccess)
                update().eq("article_id", articleId).setSql("article_like_count = article_like_count-1").update();
            return CommonResult.success(isSuccess);
        }else {
            // 如果没点赞 那就点赞
            boolean isSuccess = redissonClient.getScoredSortedSet(key).add(System.currentTimeMillis(),userId);
            if(isSuccess)
                update().eq("article_id", articleId).setSql("article_like_count = article_like_count+1").update();
            // 一直点赞一直取消，怎么办？
            sentMq(articleId.toString(),getById(articleId).getArticleUserId());
            return CommonResult.success(isSuccess);
        }
    }
    public void sentMq(String articleid, String userid) {
        Event event = new Event(TOPIC_LIKE, UserHolder.getUser().getEmail(), "article", articleid, userid);
        eventProducer.fireEventDirect(event);
    }


    @Override
    public CommonResult createArticle(ArticleDTO articleDTO) {
        UserDTO user = UserHolder.getUser();
        CommonResult result = checkArticle(articleDTO);
        if (result.getCode()!=200) {
            return result;
        }
        String categoryName = (String) categoryService.getCategoryById(articleDTO.getCategoryId()).getData();
        Article article = new Article(null, articleDTO.getTitle(),
                articleDTO.getContent(),
                0, 0, 0, DateUtil.date(), DateUtil.date(), null,
                false, articleDTO.getCategoryId(), user.getEmail(), categoryName);
        this.save(article);
        // 丢给线程池做，主线程结束
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                esArticleService.addArticle(article);
            }
        });
        //上传图片文件
        uploadImg(articleDTO.getFiles(),article);
        return null;
    }

    @Override
    public CommonResult detailArticle(Long id) {
        QueryWrapper<Article> query = new  QueryWrapper<>();
        query.eq("article_id",id);
        Article article = articleMapper.queryDetail(query);
        if (article == null) {
            return CommonResult.fail("没有这篇文章");
        }
        update().eq("article_id",id).setSql("article_view_count = article_view_count + 1").update();
        // 这篇文章是不是我喜欢的，喜欢的 liked=true
        isArticleLiked(article);
        return CommonResult.success(article);
    }


    // 查询用户的所有文章：不需要连表查询
    @Override
    public CommonResult allArticle(String useId, Integer current) {
        User user = userService.getById(useId);
        if (user == null) {
            return CommonResult.fail("没用此用户");
        }
        IPage<Article> page = query().eq("article_user_id", useId).orderByDesc("created_time").page(new Page<>(current, MAX_PAGE_SIZE));
        page.getRecords().forEach(this::isArticleLiked);
        page.getRecords().forEach(article -> {
            article.setAvatar(user.getAvatar());
            article.setName(user.getNickname());
        });
        return CommonResult.success(page);
    }

    private CommonResult checkArticle(ArticleDTO articleDTO){
        if (articleDTO.getTitle().length() > 40) {
            return CommonResult.fail("标题最多不允许超过40个字符");
        }
        try {
            BadWordUtil badWordUtil = BadWordUtil.getInstance();
            if (badWordUtil.hasBadWord(articleDTO.getTitle())) {
                List<String> badWords = badWordUtil.getBadWords(articleDTO.getTitle());
                return CommonResult.fail("标题包含违法词汇"+badWords.toString());
            }
            if(badWordUtil.hasBadWord(articleDTO.getContent())){
                String content_new = badWordUtil.replaceBadWord(articleDTO.getContent());
                articleDTO.setContent(content_new);
            }
        } catch (Exception e) {
            log.error("redis io"+e.getMessage());
        }
        CommonResult result = categoryService.getCategoryById(articleDTO.getCategoryId());
        if (result.getCode() != 200) {
            return result;
        }
        if (articleDTO.getFiles() != null) {
            if (articleDTO.getFiles().size() > 9) {
                return CommonResult.fail("最多上传9张图片!");
            }
            for (int i = 0; i < articleDTO.getFiles().size(); i++) {
                if (!MyFileUtil.sizeCheck(articleDTO.getFiles().get(i), 2)) {
                    return CommonResult.fail("每张图片大小应为2MB以内!");
                }
            }
        }
        return CommonResult.success(" ");

    }


    private CommonResult uploadImg(List<MultipartFile> files, Article article) {
        if (files == null) {
            return CommonResult.success("");
        }
        String[] urls = new String[files.size()];
        try {
            boolean flag = true;
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                String format = DateUtil.format(DateUtil.date(), "yyyy/MM/");
                String type = FileTypeUtil.getType(file.getInputStream());
                String name = article.getArticleId() + RandomUtil.randomString(10 - article.getArticleId().toString().length()) + "." + type;
                String key = "article/" + format + name;
                flag = flag && (qiNiuService.uploadFile(file.getInputStream(), key).getCode() == 200);
                urls[i] = IMAGE_UPLOAD_DIR + key;
            }
            if (flag) {
                article.setArticleImg(String.join(";", urls));
                updateById(article);
                return CommonResult.success("更新成功");
            }
            return CommonResult.fail("更新失败！");
        } catch (Exception e) {
            return CommonResult.fail("系统异常！");
        }
    }


}




