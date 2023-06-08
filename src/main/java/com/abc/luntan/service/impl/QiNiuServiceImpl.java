package com.abc.luntan.service.impl;

import com.abc.luntan.service.IQiNiuService;
import com.abc.luntan.utils.api.CommonResult;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class QiNiuServiceImpl implements IQiNiuService,InitializingBean {

    @Autowired
    private UploadManager uploadManager;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private Auth auth;

    @Value("${qiniu.Bucket}")
    private String bucket;

    StringMap putPolicy;

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody",
                "{\"key\":\"$(key)\"," +
                        "\"hash\":\"$(etag)\"," +
                        "\"bucket\":\"$(bucket)\"," +
                        "\"fsize\":$(fsize)," +
                        "\"width\":$(image.Info.width)," +
                        "\"height\":$(image.Info.height)}");
    }

    @Override
    public CommonResult uploadFile(File file, String key) throws QiniuException {
        String uploadToken = getUploadToken();
        Response response = this.uploadManager.put(file, key, uploadToken);
        int retry = 0;
        //重复三次
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, key, getUploadToken());
            retry++;
        }
        if (response.isOK()) {
            return CommonResult.success(response.bodyString());
        } else {
            return CommonResult.fail("上传失败");
        }
    }

    @Override
    public CommonResult uploadFile(InputStream inputStream, String key) throws IOException {
        String uploadToken = getUploadToken();
        Response response = this.uploadManager.put(inputStream, key, uploadToken, null, null);
        int retry = 0;
        //重复三次
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(inputStream, key, uploadToken, null, null);
            retry++;
        }
        inputStream.close();
        if (response.isOK()) {
            return CommonResult.success(response.bodyString());
        } else {
            return CommonResult.fail("上传失败");
        }
    }

    @Override
    public CommonResult delete(String key) throws QiniuException {
        Response response = this.bucketManager.delete(this.bucket, key);
        int retry = 0;
        //重复三次
        while (response.needRetry() && retry < 3) {
            response = this.bucketManager.delete(this.bucket, key);
            retry++;
        }
        if (response.isOK()) {
            return CommonResult.success(response.bodyString());
        } else {
            return CommonResult.fail("删除失败");
        }
    }

    /**
     * 获取上传token
     *
     * @return
     */
    private String getUploadToken() {
        return this.auth.uploadToken(bucket, null, 3600, putPolicy);
    }
}
