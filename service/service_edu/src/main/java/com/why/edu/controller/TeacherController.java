package com.why.edu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.Result;
import com.why.edu.entity.Teacher;
import com.why.edu.entity.condition.TeacherCondition;
import com.why.edu.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author WangHenYu
 * @since 2022-09-08
 */
@Api(description = "教师管理")
@RestController
@RequestMapping("/eduservice/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/all")
    @ApiOperation("获取所有教师列表")
    public Result getAllTeacher(){
        List<Teacher> teachers = teacherService.list(null);
        return Result.ok("list",teachers);
    }


    @GetMapping("/page/{current}/{limit}")
    @ApiOperation("教师列表分页查询")
    public Result getTeacherByPage(@PathVariable("current")int current,
                                   @PathVariable("limit") int limit){
        Map<String,Object> map = teacherService.queryTeacherByPage(current,limit);
        return Result.ok().data(map);
    }


    @PostMapping("/page/condition/{current}/{limit}")
    @ApiOperation("条件分页查询")
    public Result getPageByCondition(@PathVariable("current") int current,
                                     @PathVariable("limit") int limit,
                                     @RequestBody(required = false) TeacherCondition condition){
        Page<Teacher> teacherPage = new Page<>(current,limit);
        teacherService.queryPage(teacherPage,condition);
        Map<String,Object> map = new HashMap<>();
        map.put("total",teacherPage.getTotal());
        map.put("list",teacherPage.getRecords());
        return Result.ok().data(map);
    }


    @DeleteMapping("/delete/{id}")
    @ApiOperation("逻辑删除教师")
    public Result deleteById(
            @ApiParam(name = "id",value = "教师ID",required = true)
            @PathVariable("id")String teacherId){
        boolean removed = teacherService.removeByTeacherId(teacherId);
        return removed ? Result.ok() : Result.error();
    }

    @ApiOperation("添加教师")
    @PostMapping("/add")
    public Result addTeacher(@RequestBody Teacher teacher){
        if (Objects.isNull(teacher.getName()) || Objects.isNull(teacher.getLevel()))
            return Result.error().message("名字或头衔为必填项");
        if (teacher.getSort()!=null && teacher.getSort()<0)
            return Result.error().message("sort不能小于0");
        boolean save = teacherService.save(teacher);
        return save ? Result.ok() : Result.error();
    }

    @ApiOperation("根据id查询教师")
    @GetMapping("{id}")
    public Result getById(@PathVariable("id")String id){
        Teacher teacher = teacherService.getById(id);
        return Result.ok().data("teacher",teacher);
    }

    @ApiOperation("修改教师信息")
    @PostMapping("update")
    public Result updateTeacher(@RequestBody Teacher teacher){
        boolean update = teacherService.updateByTeacher(teacher);
        return update ? Result.ok() : Result.error();
    }

}

