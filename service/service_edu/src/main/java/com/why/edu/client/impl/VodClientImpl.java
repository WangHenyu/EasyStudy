package com.why.edu.client.impl;

import com.why.commonutils.Result;
import com.why.edu.client.VodClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VodClientImpl implements VodClient {

    @Override // 服务调用失败则执行此回调
    public Result deleteVideo(String videoSourceId) {
        return Result.error().message("视频删除出错了,执行了熔断器");
    }

    @Override
    public Result deleteMulti(List<String> ids) {
        return Result.error().message("视频删除出错了,执行了熔断器");
    }
}
