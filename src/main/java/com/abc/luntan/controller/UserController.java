package com.abc.luntan.controller;


import com.abc.luntan.dto.LoginFormDto;
import com.abc.luntan.dto.UpdateGroup;
import com.abc.luntan.dto.UserDTO;
import com.abc.luntan.entity.User;
import com.abc.luntan.service.UserService;
import com.abc.luntan.utils.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;



    @ApiOperation(value = "用户注册",notes = "只有email和密码")
    @ApiImplicitParam(name = "LoginFormDto",value = "用户的注册信息对象",dataType ="LoginFormDto",required = true)
    @PostMapping("/register")
    public CommonResult register(@RequestBody LoginFormDto loginFormDto) {
        return userService.register(loginFormDto);
    }

    @ApiOperation(value = "发送验证码")
    @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String", required = true)
    @GetMapping("/sendCode")
    public CommonResult sendCode(@RequestParam("email") String email) {
        return userService.sendCode(email);
    }


    @ApiImplicitParam(name = "LoginFormDto",value = "用户的登录信息对象",dataType ="LoginFormDto",required = true)
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public CommonResult login(@RequestBody LoginFormDto loginFormDto) {
        return userService.login(loginFormDto);
    }

    @ApiOperation(value = "用户是否登录")
    @GetMapping("/isLogin")
    public CommonResult isLogin() {
        return userService.isLogin();
    }

    //get put delete 是幂等性操作
    @ApiOperation(value = "退出登录", notes = "不需传入参数，代token")
    @PutMapping("/logout")
    public CommonResult logout() {
        return userService.logout();
    }


    @ApiImplicitParam(name = "User", value = "用户User对象", dataType = "User", required = false)
    @ApiOperation(value = "编辑用户信息", notes = "需要传入nickname(昵称),sex(性别)")
    @PostMapping("/edit")
    public CommonResult edit(@RequestBody UserDTO user) throws InterruptedException {
        return userService.editUserInfo(user);
    }


    @ApiImplicitParam(name = "User", value = "用户User对象", dataType = "User", required = false)
    @ApiOperation(value = "编辑用户信息", notes = "需要传入nickname(昵称),sex(性别)")
    @PostMapping("/edit1")
    public CommonResult edit1(@Validated(UpdateGroup.class) @RequestBody UserDTO user) throws InterruptedException {
        return userService.editUserInfo(user);
    }




    @ApiOperation(value = "重置密码", notes = "需要传入oldPassword,newPassword")
    @PostMapping("/resetPassword")
    public CommonResult resetPassword(@RequestBody Map<String,String> ps) {
        return userService.resetPassword(ps.get("oldPassword"),ps.get("newPassword"));
    }

    @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    @ApiOperation(value = "通过id查用户信息", notes = "传入id（即email），返回头像地址，nickname等基本信息")
    @GetMapping("/query/{userId}")
    public CommonResult queryUserInfo(@PathVariable("userId") String userId) {
        return userService.queryUserInfo(userId);
    }

    @ApiImplicitParam(name = "file", value = "图片对象", dataType = "MultipartFile", required = true)
    @ApiOperation(value = "上传头像", notes = "需要前端做校验，图片后缀，大小（暂定不超过1m）")
    @PostMapping("/updateAvatar")
    public CommonResult uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return userService.updateAvatar(file, request);
    }

}
