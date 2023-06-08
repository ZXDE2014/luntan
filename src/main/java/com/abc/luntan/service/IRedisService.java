package com.abc.luntan.service;

import com.abc.luntan.utils.api.CommonResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IRedisService {
    CommonResult updateBadwords(List<String> badwords);
}
