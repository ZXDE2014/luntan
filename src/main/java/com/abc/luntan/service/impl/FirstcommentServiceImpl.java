package com.abc.luntan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.abc.luntan.Event.Event;
import com.abc.luntan.Event.EventProducer;
import com.abc.luntan.dto.FirstcommentDTO;
import com.abc.luntan.entity.Article;
import com.abc.luntan.entity.User;
import com.abc.luntan.service.ArticleService;
import com.abc.luntan.service.UserService;
import com.abc.luntan.utils.BadWordUtil;
import com.abc.luntan.utils.RedisConstants;
import com.abc.luntan.utils.SystemConstants;
import com.abc.luntan.utils.UserHolder;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abc.luntan.entity.Firstcomment;
import com.abc.luntan.service.FirstcommentService;
import com.abc.luntan.mapper.FirstcommentMapper;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.abc.luntan.utils.RabbitConstants.TOPIC_COMMENT;
import static com.abc.luntan.utils.RabbitConstants.TOPIC_LIKE;
import static com.abc.luntan.utils.RedisConstants.FIRST_COMMENT_LIKED_KEY;

/**
 *
 */
@Service
public class FirstcommentServiceImpl extends ServiceImpl<FirstcommentMapper, Firstcomment>
    implements FirstcommentService{

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private FirstcommentMapper firstcommentMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private EventProducer eventProducer;

    @Override
    public CommonResult queryNewFirstComment(Integer current, Integer articleId) {
        QueryChainWrapper<Firstcomment> chainWrapper = query().eq("first_comment_article_id", articleId).orderByDesc("first_comment_created_time");
        IPage<Firstcomment> pages = firstcommentMapper.listJoinInfoPages(new Page<Firstcomment>(current, SystemConstants.MAX_PAGE_SIZE), chainWrapper);
        pages.getRecords().forEach(this::isFirstCommentLiked);
        return CommonResult.success(pages);
    }

    @Override
    public CommonResult queryHotFirstComment(Integer current, Integer articleId) {
        QueryWrapper<Firstcomment> wrapper = new QueryWrapper<>();
        wrapper.eq("first_comment_article_id", articleId).orderByDesc("first_comment_like_count").orderByAsc("first_comment_id");
        IPage<Firstcomment> firstCommentIPage = firstcommentMapper.listJoinInfoPages(new Page<>(current, SystemConstants.MAX_PAGE_SIZE), wrapper);
        firstCommentIPage.getRecords().forEach(this::isFirstCommentLiked);
        return CommonResult.success(firstCommentIPage);
    }

    @Override
    public CommonResult detailComment(Long id) {
        Firstcomment firstcomment = getById(id);
        if (firstcomment==null) {
            return CommonResult.fail("该评论不存在");
        }
        isFirstCommentLiked(firstcomment);
        User user = userService.getById(firstcomment.getFirstCommentUserId());
        firstcomment.setAvatar(user.getAvatar());
        firstcomment.setName(user.getNickname());
        return CommonResult.success(firstcomment);
    }

    @Override
    @Transactional
    public CommonResult createFirstComment(FirstcommentDTO firstcommentDTO) {
        BadWordUtil badWordUtil = BadWordUtil.getInstance();
        List<String> badWords = badWordUtil.getBadWords(firstcommentDTO.getFirstCommentContent());
        if (!badWords.isEmpty()) {
            return CommonResult.fail("内容包含敏感词:"+badWords.toString());
        }

        Firstcomment firstcomment = new Firstcomment(
                firstcommentDTO.getFirstCommentArticleId(),
                UserHolder.getUser().getEmail(),
                firstcommentDTO.getFirstCommentContent()
        );
        firstcomment.setFirstCommentCreatedTime(DateUtil.date());
        boolean save = this.save(firstcomment);
        boolean update = articleService.update().eq("article_id", firstcommentDTO.getFirstCommentArticleId())
                .setSql("article_comment_count = article_comment_count + 1")
                .update();
        if(save && update){
            sentMq(firstcomment.getFirstCommentArticleId().toString(),
                    articleService.getById(firstcommentDTO.getFirstCommentArticleId()).getArticleUserId());
            firstcomment.setAvatar(UserHolder.getUser().getAvatar());
            firstcomment.setName(UserHolder.getUser().getNickName());
            firstcomment.setLiked(false);
            return CommonResult.success(firstcomment);
        }
        return CommonResult.fail("系统繁忙，请稍后再试");
    }

    public void sentMq(String commentId, String userid) {
        Event event = new Event(TOPIC_COMMENT, UserHolder.getUser().getEmail(), "article", commentId, userid);
        eventProducer.fireEventDirect(event);
    }


    @Override
    public CommonResult likeFirstComment(Long id) {
        String email = UserHolder.getUser().getEmail();

        String key = FIRST_COMMENT_LIKED_KEY+id;
        Double score = redissonClient.getScoredSortedSet(key).getScore(email);
        if (score!= null) {
            //已经点赞
            boolean remove = redissonClient.getScoredSortedSet(key).remove(email);
            if(remove){
                update().eq("first_comment_id",id).setSql("first_comment_like_count = first_comment_like_count - 1").update();
            }
        }else {
            //未点赞
            boolean add = redissonClient.getScoredSortedSet(key).add(System.currentTimeMillis(),email);
            if(add){
                update().eq("first_comment_id",id).setSql("first_comment_like_count = first_comment_like_count + 1").update();
            }
        }


        return CommonResult.success(" ");
    }

/*    public void sentMq(String articleid, String userid) {
        Event event = new Event(TOPIC_COMMENT, UserHolder.getUser().getEmail(), "article", articleid, userid);
        eventProducer.fireEvent(event);
    }*/
    private void isFirstCommentLiked(Firstcomment firstcomment) {
        //1获取登录用户
        if (UserHolder.getUser() == null) {
            return;
        }
        String userId = UserHolder.getUser().getEmail();
        String key = FIRST_COMMENT_LIKED_KEY + firstcomment.getFirstCommentId();
        Double score = redissonClient.getScoredSortedSet(key).getScore(userId);
        firstcomment.setLiked(score != null);
    }
}




