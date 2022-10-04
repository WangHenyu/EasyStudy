package com.why.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.condition.CommentCondition;
import com.why.edu.entity.vo.CommentVo;

import java.util.Map;

/**
 * <p>
 * 评论 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-25
 */
public interface CommentService extends IService<Comment> {

    Map<String, Object> queryCommentPage(String courseId,Page<Comment> commentPage);

    boolean saveComment(String userId, CommentVo commentVo);

    Map<String,Object> queryCommentPage(int current,int limit, CommentCondition condition);

    int getCountByDate(String date);
}
