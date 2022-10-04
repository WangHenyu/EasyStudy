package com.why.oss.controller;

import com.why.commonutils.Result;
import com.why.oss.service.OssService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@Api(description = "文件上传")
@RequestMapping("/eduoss")
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload/teacher/avatar")
    @ApiOperation("上传教师头像")
    public Result uploadTeacherAvatar(MultipartFile file){
        String url = ossService.uploadTeacherAvatar(file);
        return Result.ok().data("url",url);
    }

    @PostMapping("/upload/course/cover")
    @ApiOperation("上传课程封面")
    public Result uploadCourseCover(MultipartFile file){
        String url = ossService.uploadCourseCover(file);
        return Result.ok().data("url",url);
    }

    @PostMapping("/upload/user/avatar")
    @ApiOperation("上传用户头像")
    public Result uploadUserAvatar(MultipartFile file){
        String url = ossService.uploadUserAvatar(file);
        return Result.ok().data("url",url);
    }

    @PostMapping("/upload/banner")
    @ApiOperation("上传轮播图")
    public Result uploadBanner(MultipartFile file){
        String url = ossService.uploadBanner(file);
        return Result.ok().data("url",url);
    }

}
