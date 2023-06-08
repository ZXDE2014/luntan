package com.abc.luntan.service;

import com.abc.luntan.entity.Follow;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface FollowService extends IService<Follow> {

    CommonResult isFollow(String userId);

    CommonResult follow(String userId, boolean isFollow);
}
