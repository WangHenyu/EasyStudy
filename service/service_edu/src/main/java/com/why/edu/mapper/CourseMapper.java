package com.why.edu.mapper;

import com.why.edu.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.why.edu.entity.condition.CourseCondition;
import com.why.edu.entity.vo.CourseVo;
import com.why.edu.entity.vo.PublishCourseVo;
import com.why.edu.entity.vo.front.CourseDetailVo;
import com.why.servicebase.entity.vo.ClientCourseVo;
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
public interface CourseMapper extends BaseMapper<Course> {

    PublishCourseVo selectPublishCourse(String courseId);

    List<CourseVo> selectCourseListByPage(@Param("current") int current,
                                          @Param("limit") int limit,
                                          @Param("condition") CourseCondition condition);

    CourseDetailVo selectCourseDetails(String courseId);

    ClientCourseVo selectCourseAndTeacherNameById(String courseId);

    Integer updateViewNumByVideoId(String videoId);

    Integer updateBuyNumById(String courseId);

    List<Course> selectIndexCourseData();

}
