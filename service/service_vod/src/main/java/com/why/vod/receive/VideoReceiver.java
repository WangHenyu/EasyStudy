package com.why.vod.receive;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.why.commonconst.RabbitConst;
import com.why.commonutils.Result;
import com.why.servicebase.exception.EduException;
import com.why.vod.utils.VodClientUtil;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.why.commonconst.RabbitConst.*;

@Component
public class VideoReceiver {

    // 使用RabbitListener注解不能返回任何值
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = QUEUE_VIDEO_DELETE),
            exchange = @Exchange(name = EXCHANGE_DIRECT_VIDEO),
            key = {ROUTING_KEY_VIDEO_DELETE}
    ))
    public void deleteVideo(String videoSourceId){
        System.out.println("rabbitMq异步删除视频");
        try{
            DefaultAcsClient client = VodClientUtil.getClient();
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoSourceId);
            // 删除视频
            client.getAcsResponse(request);
            return;
        }catch(Exception exception){
            exception.printStackTrace();
            throw new EduException(20001,"视频删除失败");
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = QUEUE_VIDEO_DELETE_MULTI),
            exchange = @Exchange(name = EXCHANGE_DIRECT_VIDEO),
            key = {ROUTING_KEY_VIDEO_DELETE_MULTI}
    ))
    public void deleteVideoMulti(List<String> ids){
        System.out.println("异步批量删除视频");
        if (ids.size() == 0) return;
        StringBuffer buffer = new StringBuffer();
        ids.stream().forEach(id-> {
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
        return;
    }

}
