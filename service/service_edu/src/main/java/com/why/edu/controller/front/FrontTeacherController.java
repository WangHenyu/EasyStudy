package com.why.edu.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.why.commonutils.Result;
import com.why.edu.entity.Course;
import com.why.edu.entity.Teacher;
import com.why.edu.service.CourseService;
import com.why.edu.service.TeacherService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/eduservice/front/teacher")
public class FrontTeacherController {

    @Autowired
    private TeacherService teacherService;
    @Autowired
    private CourseService courseService;

    @GetMapping("/page/{current}/{limit}")
    @ApiOperation("前台讲师分页查询接口")
    public Result queryAllTeacherByPage(@PathVariable("current") int current,
                                        @PathVariable("limit") int limit){
        Map<String, Object> map = teacherService.queryTeacherFront(current, limit);
        return Result.ok().data(map);
    }

    @GetMapping("/detail/{teacherId}")
    @ApiOperation("根据Id查询教师详情信息")
    public Result queryTeacherDetail(@PathVariable("teacherId") String teacherId){
        Teacher teacher = teacherService.getById(teacherId);
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Course::getTeacherId,teacherId);
        List<Course> courseList = courseService.list(wrapper);
        return Result.ok().data("teacher",teacher).data("courseList",courseList);
    }
}
