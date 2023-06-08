package com.abc.luntan.service;

import com.abc.luntan.dto.LoginFormDto;
import com.abc.luntan.dto.UserDTO;
import com.abc.luntan.entity.User;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 *
 */
public interface UserService extends IService<User> {

    CommonResult login(LoginFormDto loginFormDto);

    CommonResult isLogin();

    CommonResult logout();

    CommonResult queryUserInfo(String userId);

    CommonResult editUserInfo(UserDTO user);

    CommonResult register(LoginFormDto user);

    CommonResult resetPassword(String oldPassword, String password);

    CommonResult updateAvatar(MultipartFile file, HttpServletRequest request);

    CommonResult sendCode(String email);
}
