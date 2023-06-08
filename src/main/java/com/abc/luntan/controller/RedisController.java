package com.abc.luntan.controller;

import com.abc.luntan.service.IRedisService;
import com.abc.luntan.utils.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/redis")
@Api(tags = "redis模块")
public class RedisController {
    @Autowired
    private IRedisService service;

    @ApiImplicitParam(name = "Badwords", value = "敏感词",required = true, dataType="List")
    @PostMapping("/badWords")
    @ApiOperation(value = "上传系统的敏感词")
    public CommonResult uploadBadwords(@RequestParam(required = true,value = "Badwords") List<String> strings) {
        return service.updateBadwords(strings);
    }
}
