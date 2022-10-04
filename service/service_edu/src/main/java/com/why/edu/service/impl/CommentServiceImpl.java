package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.client.UserClient;
import com.why.edu.entity.Comment;
import com.why.edu.entity.condition.CommentCondition;
import com.why.edu.entity.vo.CommentVo;
import com.why.edu.mapper.CommentMapper;
import com.why.edu.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.servicebase.entity.vo.ClientUserInfoVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 评论 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-25
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserClient userClient;

    @Override
    public Map<String, Object> queryCommentPage(String courseId,Page<Comment> commentPage) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getCourseId,courseId);
        wrapper.orderByDesc(Comment::getIsTop).orderByDesc(Comment::getGmtCreate);
        baseMapper.selectPage(commentPage,wrapper);

        Map<String,Object> map = new HashMap<>();
        map.put("size",commentPage.getSize());
        map.put("pages",commentPage.getPages());
        map.put("total",commentPage.getTotal());
        map.put("hasNext",commentPage.hasNext());
        map.put("current",commentPage.getCurrent());
        map.put("commentList",commentPage.getRecords());
        map.put("hasPrevious",commentPage.hasPrevious());
        return map;
    }

    @Override
    public boolean saveComment(String userId, CommentVo commentVo) {
        // 根据用户ID获取用户信息
        ClientUserInfoVo userInfo= userClient.queryUserInfoById(userId);
        // 封装评论信息
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentVo,comment);
        comment.setUserId(userId);
        comment.setAvatar(userInfo.getAvatar());
        comment.setNickname(userInfo.getNickname());
        int insert = baseMapper.insert(comment);
        return insert > 0;
    }

    @Override
    public Map<String, Object> queryCommentPage(int current,int limit, CommentCondition condition) {
        current = (current-1)*limit;
        List<Map<String, Object>> commentList = baseMapper.selectPageCondition(current, limit, condition);
        int count = baseMapper.selectCountCondition(condition);
        Map<String,Object> map = new HashMap<>();
        map.put("total",count);
        map.put("commentList",commentList);
        return map;
    }

    @Override
    public int getCountByDate(String date) {
        int count = baseMapper.selectCountByDate(date);
        return count;
    }
}
