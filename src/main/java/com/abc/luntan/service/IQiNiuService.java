package com.abc.luntan.service;

import com.abc.luntan.utils.api.CommonResult;
import com.qiniu.common.QiniuException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface IQiNiuService {
    CommonResult uploadFile(File file, String key) throws QiniuException;

    CommonResult uploadFile(InputStream inputStream, String key) throws IOException;

    CommonResult delete(String key) throws QiniuException;
}
