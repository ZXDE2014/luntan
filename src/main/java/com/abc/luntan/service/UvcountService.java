package com.abc.luntan.service;

import com.abc.luntan.entity.Uvcount;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface UvcountService extends IService<Uvcount> {

    CommonResult getDay();

    CommonResult getAll();
}