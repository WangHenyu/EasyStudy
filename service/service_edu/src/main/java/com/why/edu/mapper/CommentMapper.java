package com.why.edu.mapper;

import com.why.edu.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.why.edu.entity.condition.CommentCondition;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评论 Mapper 接口
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-25
 */
public interface CommentMapper extends BaseMapper<Comment> {

    List<Map<String,Object>> selectPageCondition(@Param("current") int current,
                                                 @Param("limit") int limit,
                                                 @Param("condition") CommentCondition condition);

    int selectCountCondition(@Param("condition") CommentCondition condition);

    int selectCountByDate(String date);

    void deleteByCourseIds(@Param("courseIds") List<String> courseIds);
}
