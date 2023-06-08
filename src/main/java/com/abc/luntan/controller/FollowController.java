package com.abc.luntan.controller;


import com.abc.luntan.service.FollowService;
import com.abc.luntan.utils.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "关注相关接口")
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Autowired
    private FollowService followService;

    @ApiOperation(value = "关注用户或者取关用户", notes = "如果取消关注，传入FALSE；反正传入 true")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "目标用户ID", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "isFollow", value = "是否关注", required = true, dataType = "boolean", paramType = "path")
    })
    @PostMapping("")
    public CommonResult follow(@RequestParam("userId") String userId, @RequestParam("isFollow") boolean isFollow) {
        return followService.follow(userId, isFollow);
    }


    @ApiImplicitParam(name = "userId", value = "目标用户id",required = true, dataType="String")
    @ApiOperation(value = "是否关注对方")
    @GetMapping("/isFollow")
    public CommonResult isFollow(@RequestParam("userId") String userId ){
        return followService.isFollow(userId);
    }



}
