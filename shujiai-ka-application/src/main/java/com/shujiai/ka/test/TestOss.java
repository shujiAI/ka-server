package com.shujiai.ka.test;

import com.aliyun.oss.model.OSSObject;
import com.shujiai.base.context.Context;
import com.shujiai.base.service.OssObjectHandler;
import com.shujiai.base.service.oss.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class TestOss {
    @Autowired
    private OssService ossService;

    public String uploadOssFile(Context context, String appId, String version, MultipartFile file) {

        try {
            return ossService.uploadOssObject(
                    context.getTenantId(),
                    file.getOriginalFilename(),
                    file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getOssObject() throws FileNotFoundException {
//        final String key = ossService.uploadOssObject("F84478D318B14BDA98EA5912A2B76EFA", "images/a1.jpg", new FileInputStream("C:\\hxh\\文档\\HelloOss\\a1.jpg"));
//        System.out.println("key = " + key);

        String url = "/60511DDBA5FE493E9F1973F577504CF2/测试申报表.xlsx";
        try {
            ossService.getOSSObject(url, new OssObjectHandler() {
                @Override
                public void handle(OSSObject ossObject) {
                    try {
                        InputStream objectContent = ossObject.getObjectContent();
                        Files.copy(objectContent, Paths.get("D:\\AppGallery\\Downloads\\a2.jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
