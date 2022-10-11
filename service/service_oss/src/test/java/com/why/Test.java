package com.why;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;

public class Test {
    public static void main(String[] args) throws Exception {

        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
//        String endpoint = "oss-cn-hangzhou.aliyuncs.com";
        String accessKeyId = "LTAI5tMw2RMuuA1caZZJM6qf";
        String accessKeySecret = "ZyYXwFac07Xm2uw4DC8l8DoBBZDdft";
        String bucketName = "why-server-bucket";
        String objectName = "avatar/98417415698144b3a81f497be5920111-file.png";

        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 删除文件或目录
            ossClient.deleteObject(bucketName, objectName);
            System.out.println("删除成功");
        } catch (OSSException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
