package com.why.edu.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.why.commonutils.Result;
import com.why.edu.entity.Comment;
import com.why.edu.entity.Course;
import com.why.edu.entity.Teacher;
import com.why.edu.service.CommentService;
import com.why.edu.service.CourseService;
import com.why.edu.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/eduservice/front")
public class FrontIndexController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private TeacherService teacherService;

    @GetMapping("/index/data")
    public Result queryIndexData(){
        List<Course> courseList = courseService.getIndexCourseData();
        LambdaQueryWrapper<Teacher> teacherWrapper = new LambdaQueryWrapper<>();
        teacherWrapper.orderByDesc(Teacher::getLevel).last("limit 4");
        List<Teacher> teacherList = teacherService.list(teacherWrapper);
        return Result.ok().data("courseList",courseList).data("teacherList",teacherList);
    }

}
