package com.why.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.why.commonutils.Result;
import com.why.servicebase.exception.EduException;
import com.why.vod.service.VodService;
import com.why.vod.utils.VodClientUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

import static com.why.vod.utils.VodConstUtil.ACCESS_KEY_ID;
import static com.why.vod.utils.VodConstUtil.ACCESS_KEY_SECRET;

@Service
public class VodServiceImpl implements VodService {

    @Override
    public String upload(MultipartFile file) {

        String accessKeyId = ACCESS_KEY_ID;
        String accessKeySecret = ACCESS_KEY_SECRET;

        String originalFilename = file.getOriginalFilename();
        String title = originalFilename.substring(0,originalFilename.lastIndexOf("."));
        try{
            InputStream inputStream = file.getInputStream();
            // 创建流式上传的request
            UploadStreamRequest request = new UploadStreamRequest(accessKeyId, accessKeySecret, title, originalFilename, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            // 返回结果
            UploadStreamResponse response = uploader.uploadStream(request);
            if (response.isSuccess())
                return response.getVideoId();
            else {
                // 如果设置回调URL无效,不影响视频上传,可以返回VideoId同时会返回错误码
                // 其他情况上传失败时,VideoId为空,此时需要根据返回错误码分析具体错误原因
                String code = response.getCode();
                String message = response.getMessage();
                String errorMessage = "阿里云视频点播上传错误code:" + code + "message:" + message;
                String videoId = response.getVideoId();
                if (StringUtils.isEmpty(videoId))
                    throw new EduException(20001,errorMessage);
                return response.getVideoId();
            }
        }catch(Exception exception){
            exception.printStackTrace();
            throw new EduException(20001,"视频上传失败");
        }
    }

    @Override
    public Result deleteVideo(String videoSourceId) {

        try{
            DefaultAcsClient client = VodClientUtil.getClient();
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoSourceId);
            // 删除视频
            client.getAcsResponse(request);
            return Result.ok();
        }catch(Exception exception){
            exception.printStackTrace();
            throw new EduException(20001,"视频删除失败");
        }
    }

    @Override
    public Result deleteMultiVideo(List<String> ids) {
        if (ids.size() == 0)
            return Result.ok();

        StringBuffer buffer = new StringBuffer();
        ids.stream().forEach(id-> {
            // 空字符串不拼接
            if (!StringUtils.isEmpty(id)) buffer.append(id+",");
        });
        String videoIds = buffer.substring(0,buffer.lastIndexOf(","));
        try{
            DefaultAcsClient client = VodClientUtil.getClient();
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoIds);
            client.getAcsResponse(request);
        }catch(Exception exception){
            exception.printStackTrace();
            throw new EduException(20001,"批量删除失败");
        }
        return null;
    }

    @Override
    public String getVideoAuth(String videoSourceId) {
        try{
            DefaultAcsClient client = VodClientUtil.getClient();
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            request.setVideoId(videoSourceId);
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);
            String playAuth = response.getPlayAuth();
            return playAuth;
        }
        catch(Exception exception){
            exception.printStackTrace();
            throw new EduException(20001, exception.getMessage());
        }
    }
}
