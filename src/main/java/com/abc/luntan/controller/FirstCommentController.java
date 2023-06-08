package com.abc.luntan.controller;

import com.abc.luntan.dto.FirstcommentDTO;
import com.abc.luntan.service.FirstcommentService;
import com.abc.luntan.utils.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "一级评论相关接口")
@RestController
@RequestMapping("/firstComment")
public class FirstCommentController {
    @Resource
    private FirstcommentService firstcommentService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "查询的页码数", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "articleId", value = "评论所属文章id", dataType = "Integer", required = true)
    })
    @ApiOperation(value = "根据发布时间查询文章评论", notes = "默认查询最新的十条数据")
    @GetMapping("/getNewList")
    public CommonResult queryNewComment(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                        @RequestParam(value = "articleId") Integer articleId) {
        return firstcommentService.queryNewFirstComment(current, articleId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "查询的页码数", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "articleId", value = "评论所属文章id", dataType = "Integer", required = true)
    })
    @ApiOperation(value = "根据点赞数查询文章评论", notes = "默认查询最热的十条数据")
    @GetMapping("/getHotList")
    public CommonResult queryHotComment(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                  @RequestParam(value = "articleId") Integer articleId) {
        return firstcommentService.queryHotFirstComment(current, articleId);
    }

    @ApiImplicitParam(name = "id", value = "评论的id", dataType = "Integer", required = true)
    @ApiOperation(value = "查询一级评论详细信息")
    @GetMapping("/detail/{id}")
    public CommonResult detailComment(@PathVariable("id") Long id) {
        return firstcommentService.detailComment(id);
    }

    @ApiImplicitParam(name = "firstcommentDTO", value = "评论对象，包括所属文章id，和评论内容", dataType = "FirstcommentDTO", required = true)
    @ApiOperation(value = "新增评论", notes = "一级评论，评论内容长度小于等于255")
    @PostMapping("/create")
    public CommonResult createFirstComment(@RequestBody FirstcommentDTO firstcommentDTO) {
        return firstcommentService.createFirstComment(firstcommentDTO);
    }

    @ApiImplicitParam(name = "id", value = "一级评论id", dataType = "Integer", required = true)
    @ApiOperation(value = "给评论点赞或取消")
    @PutMapping("/like/{id}")
    public CommonResult likeFirstComment(@PathVariable("id") Long id) {
        return firstcommentService.likeFirstComment(id);
    }
}