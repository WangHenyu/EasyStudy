package com.why.edu.mapper;

import com.why.edu.entity.CourseCollect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.why.edu.entity.vo.front.CollectionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程收藏 Mapper 接口
 * </p>
 *
 * @author WangHenYu
 * @since 2022-10-03
 */
public interface CourseCollectMapper extends BaseMapper<CourseCollect> {

    List<CollectionVo> selectCollectListByUserId(@Param("current") int current,
                                                 @Param("limit") int limit,
                                                 @Param("userId") String userId);
}
