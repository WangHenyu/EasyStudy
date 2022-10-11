package com.why.oss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.why.oss.service.OssService;
import com.why.oss.uitls.OssConstUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

import static com.why.oss.uitls.OssConstUtil.*;

@Service
public class OssServiceImpl implements OssService {

    @Override
    public String uploadTeacherAvatar(MultipartFile file) {
        return upload(file,"avatar");
    }

    @Override
    public String uploadCourseCover(MultipartFile file) {
        return upload(file,"courseCover");
    }

    @Override
    public String uploadBanner(MultipartFile file) {
        return upload(file,"banner");
    }

    @Override
    public String uploadUserAvatar(MultipartFile file) {
        return upload(file,"userAvatar");
    }

    private String upload(MultipartFile file, String prefix) {
        String endpoint = ENDPOINT;
        String accessKeyId = KEY_ID;
        String accessKeySecret = KEY_SECRET;
        String bucketName = BUCKET_NAME;
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString().replaceAll("-","")+ "-" + originalFilename;
            // 将文件分类存储
            fileName = prefix + "/" + fileName;
            /**
             * 创建PutObject请求
             * 1.Bucket名字
             * 2.文件名字
             * 3.上传文件输入流
             */
            ossClient.putObject(bucketName, fileName, inputStream);
            String url = "";
            url = "https://" + bucketName+ "." + endpoint+ "/" + fileName;
            return url;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
