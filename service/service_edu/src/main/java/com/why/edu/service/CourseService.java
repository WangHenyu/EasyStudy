package com.why.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.condition.CourseCondition;
import com.why.edu.entity.condition.front.FrontCourseCondition;
import com.why.edu.entity.vo.CourseVo;
import com.why.edu.entity.vo.PublishCourseVo;
import com.why.servicebase.entity.vo.ClientCourseVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
public interface CourseService extends IService<Course> {

    String saveCourse(CourseVo courseVo);

    CourseVo queryCourseInfoById(String courseId);

    boolean updateCourseInfo(CourseVo courseInfo);

    PublishCourseVo queryPublishCourse(String courseId);

    Map<String,Object> queryCourseListByPage(int current, int limit, CourseCondition condition);

    boolean multiDeleteCourseInfo(List<String> courseIds);

    Map<String, Object> queryCoursePageByCondition(Page<Course> coursePage, FrontCourseCondition condition);

    Map<String, Object> queryCourseDetails(String courseId, HttpServletRequest request);

    ClientCourseVo queryCourseAndTeacherNameById(String courseId);

    boolean updateViewNum(String videoId);

    boolean updateBuyNum(String courseId);

    List<Course> getIndexCourseData();
}
