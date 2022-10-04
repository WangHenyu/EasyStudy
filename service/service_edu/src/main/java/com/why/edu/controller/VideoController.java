package com.why.edu.controller;


import com.why.commonutils.Result;
import com.why.edu.entity.vo.VideoVo;
import com.why.edu.service.VideoService;
import com.why.servicebase.entity.vo.ClientCourseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
@RestController
@Api(description = "小节管理")
@RequestMapping("/eduservice/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @DeleteMapping("/delete/{videoId}")
    @ApiOperation("根据videoId删除小节")
    public Result deleteVideoById(@PathVariable("videoId")String videoId){

        boolean remove = videoService.removeVideoAndSource(videoId);
        return remove ? Result.ok() : Result.error();
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation("根据ID是否为空值判断是添加还是修改")
    public Result saveOrUpdate(@RequestBody VideoVo videoVo){

        boolean success = videoService.saveOrUpdateVideo(videoVo);
        return success ? Result.ok() : Result.error();
    }

    @GetMapping("/course/info/{videoId}")
    @ApiOperation("根据videoId获取课程信息")
    public ClientCourseVo queryCourseInfo(@PathVariable("videoId")String videoId){
        ClientCourseVo courseInfo = videoService.getCourseInfoByVideoId(videoId);
        return courseInfo;
    }


}

