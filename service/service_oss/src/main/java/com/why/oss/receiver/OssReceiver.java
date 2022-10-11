package com.why.oss.receiver;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.why.commonconst.RabbitConst.*;
import static com.why.oss.uitls.OssConstUtil.*;


@Slf4j
@Component
public class OssReceiver {

    // 删除图片
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name =QUEUE_OSS_DELETE),
            exchange = @Exchange(name = EXCHANGE_DIRECT_OSS),
            key = {ROUTING_KEY_OSS_DELETE}
    ))
    public void deleteOssFile(String avatarUrl){
        String endpoint = "https://" + ENDPOINT;
        String accessKeyId = KEY_ID;
        String accessKeySecret = KEY_SECRET;
        String bucketName = BUCKET_NAME;
        if (StringUtils.isEmpty(avatarUrl)) return;
        log.info("异步删除图片" + avatarUrl);
        int index = avatarUrl.indexOf("/", "https://".length());
        String fileName = avatarUrl.substring(index+1);
        // 默认头像不删除
        if ("avatar/4c8c51f40ea54ee5bf92824735fafa24-file.png".equals(fileName)) return;
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 删除文件或目录 如果要删除目录,目录必须为空
            ossClient.deleteObject(bucketName, fileName);
        } catch (OSSException oe) {
            log.error("图片删除失败：" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    // 批量删除图片
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name =QUEUE_OSS_DELETE_MULTI),
            exchange = @Exchange(name = EXCHANGE_DIRECT_OSS),
            key = {ROUTING_KEY_OSS_DELETE_MULTI}
    ))
    public void deleteOssFileMulti(List<String> fileNames){
        String endpoint = "https://" + ENDPOINT;
        String accessKeyId = KEY_ID;
        String accessKeySecret = KEY_SECRET;
        String bucketName = BUCKET_NAME;
        log.info("异步批量删除图片");
        List<String> keys = fileNames.stream().map(fileName -> {
            int index = fileName.indexOf("/", "https://".length());
            fileName = fileName.substring(index + 1);
            return fileName;
        }).filter(fileName->!StringUtils.isEmpty(fileName)).collect(Collectors.toList());

        System.out.println(keys);
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 删除文件
            // 填写需要删除的多个文件完整路径
            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(
                    // withQuiet(boolean) false:详细模式 true：简单模式（默认详细模式）
                    new DeleteObjectsRequest(bucketName).withKeys(keys).withEncodingType("url"));
            // 详细模式下为删除成功的文件列表，简单模式下为删除失败的文件列表
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            try {
                for(String obj : deletedObjects) {
                    String deleteObj =  URLDecoder.decode(obj, "UTF-8");
                    System.out.println("成功删除文件：" + deleteObj);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (OSSException oe) {
            log.error("批量删除失败:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
