package com.why.edu.mapper;

import com.why.edu.entity.Chapter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程 Mapper 接口
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
@Repository
public interface ChapterMapper extends BaseMapper<Chapter> {

    boolean deleteByCourseIds(@Param("courseIds") List<String> courseIds);
}
