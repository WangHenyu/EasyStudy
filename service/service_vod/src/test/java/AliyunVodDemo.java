import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadVideoRequest;
import com.aliyun.vod.upload.resp.UploadVideoResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.*;


import java.util.List;

public class AliyunVodDemo {

    public static void main(String[] args) throws ClientException {
        // 只能获取未加密的视频地址
        // getVideoUrl();
        // 可以获取未加密的视频凭证
         getVideoAuth("f034e71c786a445123b88d4b16a180d91dd");
        // deleteVideo();
        // upload();
    }


    /**
     * 上传视频
     */
    public static void upload(){
        String accessKeyId = "LTAI5tMw2RMuuA1caZZJM6qf";
        String accessKeySecret = "ZyYXwFac07Xm2uw4DC8l8DoBBZDdft";
        String title = "hello";
        String fileName = "C:\\Users\\uuu\\Desktop\\Java资料\\项目\\项目资料\\1-阿里云上传测试视频\\6 - What If I Want to Move Faster.mp4";

        UploadVideoRequest request = new UploadVideoRequest(accessKeyId,accessKeySecret,title,fileName);
        // 可指定分片上传时每个分片的大小，默认为2M字节
        request.setPartSize(2 * 1024 * 1024L);
        // 可指定分片上传时的并发线程数
        request.setTaskNum(1);

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadVideoResponse response = uploader.uploadVideo(request);
        if (response.isSuccess()) {
            System.out.println("VideoId=" + response.getVideoId());
        } else {
            // 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            System.out.println("VideoId=" + response.getVideoId());
            System.out.println("ErrorCode=" + response.getCode());
            System.out.println("ErrorMessage=" + response.getMessage());
        }


    }
    /**
     * 根据视频ID获取视频凭证
     */
    public static void getVideoAuth(String id) throws ClientException {
        // 初始化client
        DefaultAcsClient client = initVodClient();

        // 创建获取视频凭证的request和response
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        GetVideoPlayAuthResponse response;

        // 向request中设置视频ID
        // 未加密视频ID
        // request.setVideoId("f778ca06143b4e9a9051c41a1f7a75cc");
        // 加密视频ID
        request.setVideoId(id);
        // 获取视频数据
        response = client.getAcsResponse(request);
        System.out.println("视频凭证" + response.getPlayAuth());
        System.out.println("视频标题" + response.getVideoMeta().getTitle());
    }

    /**
     * 根据视频ID获取视频地址（加密视频无法获取）
     */
    public static void getVideoUrl() throws ClientException {
        //初始化client
        DefaultAcsClient client = initVodClient();
        //创建获取视频地址的request和response
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        GetPlayInfoResponse response;
        //向request中设置视频ID
        request.setVideoId("f778ca06143b4e9a9051c41a1f7a75cc");
        //获取视频数据
        response = client.getAcsResponse(request);
        List<GetPlayInfoResponse.PlayInfo> playInfoList = response.getPlayInfoList();
        for (GetPlayInfoResponse.PlayInfo playInfo : playInfoList) {
            System.out.println("视频地址："+playInfo.getPlayURL());
        }
        System.out.println("视频标题："+response.getVideoBase().getTitle());
    }

    /**
     * 删除视频
     */
    public static void deleteVideo() throws ClientException {

        DefaultAcsClient client = initVodClient();
        // 创建用于删除视频的request和response
        DeleteVideoRequest request = new DeleteVideoRequest();
        DeleteVideoResponse response;

        request.setVideoIds("11f0443b04004864aa8a4b5a9e0aaa60");
        // 批量删除
        // request.setVideoIds("id1,id2,id3...");
        response = client.getAcsResponse(request);
        System.out.println("删除成功"+response.getRequestId());


    }


    public static DefaultAcsClient initVodClient(){
        //点播服务接入区(固定值)
        String regionId = "cn-shanghai";
        String accessKeyId = "LTAI5tMw2RMuuA1caZZJM6qf";
        String accessKeySecret = "ZyYXwFac07Xm2uw4DC8l8DoBBZDdft";
        //获取配置
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient defaultAcsClient = new DefaultAcsClient(profile);
        return defaultAcsClient;
    }

}
