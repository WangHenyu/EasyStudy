package com.why.edu.controller;


import com.why.commonutils.Result;
import com.why.edu.entity.Course;
import com.why.edu.entity.condition.CourseCondition;
import com.why.edu.entity.vo.CourseVo;
import com.why.edu.entity.vo.PublishCourseVo;
import com.why.edu.service.CourseService;
import com.why.servicebase.entity.vo.ClientCourseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */

@RestController
@RequestMapping("/eduservice/course")
@Api(description = "课程管理")
public class CourseController {

    @Autowired
    private CourseService courseService;


    @PostMapping("/add")
    @ApiOperation("添加课程信息")
    public Result addCourse(@RequestBody CourseVo courseVo){
        String courseId = courseService.saveCourse(courseVo);
        return Result.ok("courseId",courseId);
    }


    @GetMapping("/{courseId}")
    @ApiOperation("根据课程ID查询课程信息")
    public Result getCourseInfoById(@PathVariable("courseId")String courseId){
        CourseVo courseInfo = courseService.queryCourseInfoById(courseId);
        return Result.ok().data("courseInfo",courseInfo);
    }


    @PostMapping("/update")
    @ApiOperation("修改课程信息")
    public Result updateCourseInfo(@RequestBody CourseVo courseInfo){
        boolean success = courseService.updateCourseInfo(courseInfo);
        return success ? Result.ok() : Result.error();
    }

    @GetMapping("/publish/{courseId}")
    @ApiOperation("根据ID查询课程发布信息")
    public Result queryPublishCourse(@PathVariable("courseId") String courseId){
        PublishCourseVo publishCourse = courseService.queryPublishCourse(courseId);
        return Result.ok().data("publishCourse",publishCourse);
    }

    @PostMapping("/update/{courseId}/{status}")
    @ApiOperation("修改课程状态信息")
    public Result updateStatus(@PathVariable("courseId") String courseId,
                               @PathVariable("status") String status){
        Course course = new Course();
        course.setId(courseId);
        course.setStatus(status);
        boolean update = courseService.updateById(course);
        return update? Result.ok():Result.error();
    }


    @PostMapping("/page/condition/{current}/{limit}")
    @ApiOperation("分类查询教师信息带条件")
    public Result getCourseListPage(@PathVariable("current")int current,
                                    @PathVariable("limit")int limit,
                                    @RequestBody(required = false) CourseCondition condition){
        Map<String,Object> courseData = courseService.queryCourseListByPage(current,limit,condition);
        return Result.ok().data("courseData",courseData);
    }

    @DeleteMapping("/delete/multi")
    @ApiOperation("批量删除")
    public Result multiDelete(@RequestBody List<String> courseIds){
        if (courseIds.size() == 0)
            return Result.error().message("未选择删除的课程");
        boolean remove = courseService.multiDeleteCourseInfo(courseIds);
        return remove ? Result.ok() : Result.error();
    }

    @GetMapping("/client/{courseId}")
    @ApiOperation("根据课程ID查询课程信息(远程调用)")
    public ClientCourseVo queryClientCourseInfoById(@PathVariable("courseId")String courseId){
        ClientCourseVo clientCourseVo = courseService.queryCourseAndTeacherNameById(courseId);
        return clientCourseVo;
    }

    @GetMapping("/update/view/num/{videoId}")
    public boolean updateViewNum(@PathVariable("videoId") String videoId){
        return courseService.updateViewNum(videoId);
    }
    @GetMapping("/update/buy/num/{courseId}")
    public boolean updateBuyNum(@PathVariable("courseId") String courseId){
        return courseService.updateBuyNum(courseId);
    }
}

