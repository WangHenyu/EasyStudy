package com.why.edu.mapper;

import com.why.edu.entity.Video;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.why.servicebase.entity.vo.ClientCourseVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程视频 Mapper 接口
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
@Repository
public interface VideoMapper extends BaseMapper<Video> {

    boolean deleteByCourseIds(@Param("courseIds") List<String> courseIds);

    String selectVideoSourceId(String videoId);

    List<String> selectAllVideoSourceId(@Param("courseIds") List<String> courseIds);

    ClientCourseVo selectCourseByVideoId(String videoId);
}
