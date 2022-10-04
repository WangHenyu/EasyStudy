package com.why.edu.controller;


import com.why.commonutils.Result;
import com.why.edu.entity.Chapter;
import com.why.edu.entity.vo.ChapterVo;
import com.why.edu.service.ChapterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */

@RestController
@RequestMapping("/eduservice/chapter")
@Api(description = "章节管理")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;


    @GetMapping("/{courseId}")
    @ApiOperation("根据课程Id获取章节和小节")
    public Result getChapterAndVideo(@PathVariable("courseId") String courseId){
        List<ChapterVo> chapterVoList = chapterService.getChapterAndVideoByCourseId(courseId);
        return Result.ok().data("list", chapterVoList);
    }


    @DeleteMapping("/delete/{chapterId}")
    @ApiOperation("根据章节Id删除章节")
    public Result deleteChapterById(@PathVariable("chapterId")String chapterId){
        boolean delete = chapterService.deleteChapterById(chapterId);
        return delete ? Result.ok() : Result.error();
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation("添加章节")
    public Result saveOrUpdateChapter(@RequestBody ChapterVo chapterVo){

        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(chapterVo,chapter);
        if (StringUtils.isEmpty(chapter.getId())){
            chapterService.save(chapter);
            return Result.ok();
        }
        chapterService.updateById(chapter);
        return Result.ok();
    }
}

