package com.abc.luntan.controller;


import com.abc.luntan.dto.ArticleDTO;
import com.abc.luntan.entity.Article;
import com.abc.luntan.service.ArticleService;
import com.abc.luntan.service.EsArticleService;
import com.abc.luntan.utils.api.CommonResult;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "文章相关接口")
@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private EsArticleService esArticleService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "category",value = "类别",dataType = "int",paramType = "query")
    })
    @ApiOperation(value = "查询最新文章",notes = "默认类别为0，首页10条最新文章")
    @GetMapping("/new")
    public CommonResult queryNewArticle(@RequestParam(value = "current",defaultValue = "1") Integer current,
                                        @RequestParam(value = "category",defaultValue = "0")   Integer category
    ){
        return articleService.queryNewArticle(current,category);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "页码",dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "category",value = "类别",dataType = "int",paramType = "query")
    })
    @ApiOperation(value = "过去一个月点赞最多的文章",notes = "默认类别为0，10条文章")
    @GetMapping("/hot")
    public CommonResult queryHotArticle(@RequestParam(value = "current",defaultValue = "1") Integer current,
                                        @RequestParam(value = "category",defaultValue = "0")    Integer category
    ){
        return articleService.queryHotArticle(current,category);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId",value = "文章id",dataType = "Long",required = true)
    })
    @ApiOperation(value = "给文章点赞或者取消")
    @PutMapping("/like/{articleId}")
    public CommonResult likeArticle(@PathVariable(value = "articleId") Long articleId){
        return articleService.likeArticle(articleId);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleDTO",value = "新增文章对象",dataType = "ArticleDTO",required = true)
    })
    @ApiOperation(value = "创建文章",notes = "初始化文章title，content，category,附件file")
    @PostMapping("/create")
    public CommonResult createArticle(@RequestParam String title,@RequestParam String content,
                                      @RequestParam(value = "category") Integer categoryId,
                                      @RequestParam(value = "files",required = false) List<MultipartFile> files){
        return articleService.createArticle(new ArticleDTO(title,content,categoryId,files));
    }

    @ApiImplicitParam(name = "id", value = "文章id", dataType = "Integer", required = true)
    @ApiOperation(value = "查询文章详细信息")
    @GetMapping("/detail/{id}")
    public CommonResult detailArticle(@PathVariable("id") Long id) {
        return articleService.detailArticle(id);
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "查询的页码数", dataType = "Integer", required = false),
            @ApiImplicitParam(name = "useId", value = "用户id", dataType = "String", required = true)
    })
    @ApiOperation(value = "查询某用户的所有文章")
    @GetMapping("/all")
    public CommonResult allArticle(@RequestParam("useId") String useId, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return articleService.allArticle(useId, current);
    }

    @ApiImplicitParam(name = "key", value = "检索关键字", dataType = "String", required = false)
    @ApiOperation(value = "查询文章", notes = "返回文章简单信息和id")
    @GetMapping("/search")
    public CommonResult search(@RequestParam("key") String key) {
        return esArticleService.search(key);
    }
}
