package com.why.vod.service;


import com.why.commonutils.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodService {

    String upload(MultipartFile file);

    Result deleteVideo(String videoSourceId);

    Result deleteMultiVideo(List<String> ids);

    String getVideoAuth(String videoSourceId);
}
