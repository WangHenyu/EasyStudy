package com.why.edu.controller;


import com.why.commonutils.Result;
import com.why.edu.entity.Subject;
import com.why.edu.entity.vo.SubjectVo;
import com.why.edu.service.SubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-11
 */

@RestController
@Api(description = "课程分类管理")
@RequestMapping("/eduservice/subject")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;


    @PostMapping("/add/multi")
    @ApiOperation("通过Excel批量导入课程分类")
    public Result addSubject(MultipartFile file){
        boolean success = subjectService.saveByExcel(subjectService,file);
        return success ? Result.ok() : Result.error();
    }


    @GetMapping("/all")
    @ApiOperation("查询所有分类")
    public Result getAllSubject(){
        List<SubjectVo> list = subjectService.queryAllSubject();
        return Result.ok().data("list",list);
    }

    @GetMapping("/update/{id}/{title}")
    @ApiOperation("修改课程分类")
    public Result updateSubject(@PathVariable("id")String id,
                                @PathVariable("title")String title){

        Subject subject = new Subject();
        subject.setTitle(title);
        subject.setId(id);
        boolean isUpdate = subjectService.updateById(subject);
        return isUpdate ? Result.ok() : Result.error();
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("根据id删除分类")
    public Result deleteById(@PathVariable("id") String id){
        boolean delete = subjectService.deleteById(id);
        return delete ? Result.ok() : Result.error();
    }

    @GetMapping("/statistics/data")
    @ApiOperation("获取分类课程数量前八的分类(远程调用)")
    public List<Map<String,Object>> querySubjectData(){
        List<Map<String,Object>> data = subjectService.getSubjectData();
        return data;
    }
}

