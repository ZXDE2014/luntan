package com.abc.luntan.controller;

import com.abc.luntan.service.UvcountService;
import com.abc.luntan.utils.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "系统访问量")
@RestController
@RequestMapping("/uvCount")
public class UvCountController {
    @Autowired
    private UvcountService uvCountService;

    @ApiOperation(value = "获取平台当日访问量")
    @GetMapping("/getDay")
    public CommonResult getDay() {
        return uvCountService.getDay();
    }

    @ApiOperation(value = "获取平台总访问量")
    @GetMapping("/getAll")
    public CommonResult getAll() {
        return uvCountService.getAll();
    }
}
