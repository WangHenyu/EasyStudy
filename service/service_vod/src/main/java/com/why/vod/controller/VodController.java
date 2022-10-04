package com.why.vod.controller;

import com.why.commonutils.Result;
import com.why.vod.service.VideoLogService;
import com.why.vod.service.VodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(description = "视频操作")
@RequestMapping("/eduvod")
public class VodController {

    @Autowired
    private VodService vodService;
    @Autowired
    private VideoLogService videoLogService;

    @PostMapping("/upload")
    @ApiOperation("上传视频到阿里云")
    public Result uploadVideo(MultipartFile file){
        String videoSourceId = vodService.upload(file);
        return Result.ok().data("videoSourceId",videoSourceId);
    }

    @DeleteMapping("/delete/{videoSourceId}")
    @ApiOperation("根据视频ID删除阿里云中的视频")
    public Result deleteVideo(@PathVariable("videoSourceId")String videoSourceId){
        return vodService.deleteVideo(videoSourceId);
    }


    @DeleteMapping("/delete/multi")
    @ApiOperation("批量删除视频")
    public Result deleteMulti(@RequestBody List<String> ids){
        return vodService.deleteMultiVideo(ids);
    }

    @GetMapping("/auth/{videoSourceId}/{videoId}")
    @ApiOperation("根据ID获取视频播放凭证")
    public Result getVideoAuth(@PathVariable("videoSourceId")String videoSourceId,
                               @PathVariable("videoId") String videoId,
                               HttpServletRequest request){
        String auth = vodService.getVideoAuth(videoSourceId);
        // 添加观看记录
        videoLogService.saveOrUpdateVideoLog(request,videoId,videoSourceId);
        return Result.ok().data("auth",auth);
    }

}
