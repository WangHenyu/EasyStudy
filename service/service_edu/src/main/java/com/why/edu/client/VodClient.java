package com.why.edu.client;

import com.why.commonutils.Result;
import com.why.edu.client.impl.VodClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
// value: 要调用的服务名
@FeignClient(name = "vod-service",fallback = VodClientImpl.class)
public interface VodClient {

    @DeleteMapping("/eduvod/delete/{videoSourceId}")
    Result deleteVideo(@PathVariable("videoSourceId")String videoSourceId);

    // ids多个视频的id
    @DeleteMapping("/eduvod/delete/multi")
    Result deleteMulti(@RequestBody List<String> ids);
}
