package com.why.edu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.JwtUtils;
import com.why.commonutils.Result;
import com.why.edu.entity.CourseCollect;
import com.why.edu.service.CourseCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 课程收藏 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-10-03
 */
@RestController
@RequestMapping("/eduservice/collection")
@Api(description = "课程收藏模块")
public class CourseCollectController {

    @Autowired
    private CourseCollectService collectService;

    @GetMapping("/auth/add/{courseId}")
    @ApiOperation("添加收藏")
    public Result saveCollection(@PathVariable("courseId")String courseId,
                                 HttpServletRequest request){
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        LambdaQueryWrapper<CourseCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseCollect::getCourseId,courseId).eq(CourseCollect::getUserId,userId);
        int count = collectService.count(wrapper);
        if (count>0){
            return Result.error().message("您已经收藏过该课程");
        }
        CourseCollect collect = new CourseCollect();
        collect.setCourseId(courseId);
        collect.setUserId(userId);
        boolean save = collectService.save(collect);
        return save ? Result.ok() : Result.error();
    }

    @GetMapping("/auth/page/{current}/{limit}")
    @ApiOperation("根据用户Id获取收藏列表")
    public Result queryCollectionByUserId(@PathVariable("current")int current,
                                          @PathVariable("limit")int limit,
                                          HttpServletRequest request){
        String userId = JwtUtils.getUserIdWithJwtToken(request);

        Map<String,Object> map = collectService.getCollectionPageByUserId(current, limit, userId);
        return Result.ok().data(map);
    }

    @GetMapping("/isCollect/{courseId}")
    @ApiOperation("查询用户是否收藏该课程")
    public Result queryIsCollect(@PathVariable("courseId")String courseId,HttpServletRequest request){
        boolean hasAuth = JwtUtils.checkToken(request);
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        if (StringUtils.isEmpty(userId) || !hasAuth){
            return Result.ok().data("isCollect",false);
        }
        LambdaQueryWrapper<CourseCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseCollect::getUserId,userId)
                .eq(CourseCollect::getCourseId,courseId);
        int count = collectService.count(wrapper);
        return Result.ok().data("isCollect",count>0);
    }

    @DeleteMapping("/delete/{collectId}")
    @ApiOperation("取消收藏")
    public Result deleteCollection(@PathVariable("collectId")String collectId){
        boolean remove = collectService.removeById(collectId);
        return remove ? Result.ok() : Result.error();
    }

}

