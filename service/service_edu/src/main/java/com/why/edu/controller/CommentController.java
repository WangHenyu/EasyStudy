package com.why.edu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.JwtUtils;
import com.why.commonutils.Result;
import com.why.edu.entity.Comment;
import com.why.edu.entity.condition.CommentCondition;
import com.why.edu.entity.vo.CommentVo;
import com.why.edu.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-25
 */

@RestController
@RequestMapping("/eduservice/comment")
@Api(description = "课程评论管理")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/page/{courseId}/{current}/{limit}")
    @ApiOperation("课程评论分页查询")
    public Result queryCommentPage(@PathVariable("courseId") String courseId,
                                   @PathVariable("current")int current,
                                   @PathVariable("limit")int limit){

        Page<Comment> commentPage = new Page<>(current,limit);
        Map<String,Object> map = commentService.queryCommentPage(courseId,commentPage);
        return Result.ok().data(map);
    }

    @PostMapping("/page/condition/{current}/{limit}")
    @ApiOperation("课程评论分页条件查询")
    public Result queryCommentPageCondition(@PathVariable("current") int current,
                                            @PathVariable("limit") int limit,
                                            @RequestBody(required = false) CommentCondition condition){

        Map<String, Object> map = commentService.queryCommentPage(current,limit,condition);
        return Result.ok().data(map);
    }

    @PostMapping("/save")
    @ApiOperation("添加评论")
    public Result saveComment(HttpServletRequest request,@RequestBody CommentVo commentVo){

        String userId = JwtUtils.getUserIdWithJwtToken(request);
        boolean save = commentService.saveComment(userId,commentVo);
        return save ? Result.ok() : Result.error();
    }

    @GetMapping("/update/top/{commentId}/{isTop}")
    @ApiOperation("修改是否置顶评论")
    public Result updateIsTop(@PathVariable("commentId")String commentId,
                              @PathVariable("isTop") Boolean isTop){

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setIsTop(isTop);
        boolean update = commentService.updateById(comment);
        return update ? Result.ok() : Result.error();
    }

    @DeleteMapping("/{commentId}")
    @ApiOperation("根据Id删除评论")
    public Result deleteComment(@PathVariable("commentId") String commentId){

        boolean remove = commentService.removeById(commentId);
        return remove ? Result.ok() : Result.error();
    }

    @GetMapping("/daily/count/{date}")
    @ApiOperation("根据日期获取当日评论的总数")
    public int queryCountByDate(@PathVariable("date")String date){
        int count = commentService.getCountByDate(date);
        return count;
    }
}

