package com.why.edu.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.Result;
import com.why.edu.entity.Course;
import com.why.edu.entity.condition.front.FrontCourseCondition;
import com.why.edu.service.CourseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/eduservice/front/course")
public class FrontCourseController {

    @Autowired
    private CourseService courseService;


    @PostMapping("/page/{current}/{limit}")
    @ApiOperation("前台分页获取课程信息")
    public Result queryCoursePage(@PathVariable("current") int current,
                                  @PathVariable("limit") int limit,
                                  @RequestBody FrontCourseCondition condition){
        Page<Course> coursePage = new Page<>(current,limit);
        Map<String,Object> map = courseService.queryCoursePageByCondition(coursePage,condition);
        return Result.ok().data(map);
    }

    @GetMapping("/detail/{courseId}")
    @ApiOperation("根据ID获取课程详细信息")
    public Result queryCourseDetails(@PathVariable("courseId")String courseId, HttpServletRequest request){
        Map<String,Object> map = courseService.queryCourseDetails(courseId,request);
        return Result.ok().data(map);
    }
}
