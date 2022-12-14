package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.commonutils.JwtUtils;
import com.why.edu.client.OrderClient;
import com.why.edu.entity.Course;
import com.why.edu.entity.CourseDescription;
import com.why.edu.entity.condition.CourseCondition;
import com.why.edu.entity.condition.front.FrontCourseCondition;
import com.why.edu.entity.vo.ChapterVo;
import com.why.edu.entity.vo.CourseVo;
import com.why.edu.entity.vo.PublishCourseVo;
import com.why.edu.entity.vo.front.CourseDetailVo;
import com.why.edu.mapper.*;
import com.why.edu.service.ChapterService;
import com.why.edu.service.CourseService;
import com.why.servicebase.entity.vo.ClientCourseVo;
import com.why.servicebase.exception.EduException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.why.commonconst.RabbitConst.*;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseDescriptionMapper descriptionMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private ChapterService chapterService;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public String saveCourse(CourseVo courseVo) {
        String title = courseVo.getTitle();
        String teacherId = courseVo.getTeacherId();
        String subjectId = courseVo.getSubjectId();
        String subjectParentId = courseVo.getSubjectParentId();
        if (StringUtils.isEmpty(title))
            throw new EduException(20001,"课程标题不能为空");
        if (StringUtils.isEmpty(teacherId))
            throw new EduException(20001,"课程讲师不能为空");
        if (StringUtils.isEmpty(subjectId) || StringUtils.isEmpty(subjectParentId))
            throw new EduException(20001,"课程分类不能为空");
        Course course = new Course();
        BeanUtils.copyProperties(courseVo,course);
        int insertCount = baseMapper.insert(course);
        if (insertCount == 0)
            throw new EduException(20001,"课程信息添加失败");
        String courseId = course.getId();
        CourseDescription description = new CourseDescription();
        description.setDescription(courseVo.getDescription());
        description.setId(courseId);
        int saveCount = descriptionMapper.insert(description);
        if (saveCount == 0)
            throw new EduException(20001,"课程描述信息添加失败");
        return courseId;
    }


    @Override
    public CourseVo queryCourseInfoById(String courseId) {
        // 查询课程信息
        Course course = baseMapper.selectById(courseId);
        if(Objects.isNull(course))
            throw new EduException(20001,"找不到相关课程");
        // 查询课程描述
        CourseDescription description = descriptionMapper.selectById(courseId);
        // 封装课程基本信息
        CourseVo courseInfo = new CourseVo();
        BeanUtils.copyProperties(course,courseInfo);
        BeanUtils.copyProperties(description,courseInfo);
        return courseInfo;
    }


    @Override
    @Transactional
    public boolean updateCourseInfo(@RequestBody CourseVo courseInfo) {
        if (courseInfo==null || StringUtils.isEmpty(courseInfo.getId()))
            return false;
        Course course = new Course();
        CourseDescription description = new CourseDescription();
        BeanUtils.copyProperties(courseInfo,course);
        BeanUtils.copyProperties(courseInfo,description);
        // 获取旧的课程封面
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Course::getCover);
        wrapper.eq(Course::getId,course.getId());
        Course oldCourse = baseMapper.selectOne(wrapper);
        // 异步删除旧封面
        if (!StringUtils.isEmpty(oldCourse.getCover()) && !oldCourse.getCover().equals(courseInfo.getCover())) {
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_OSS, ROUTING_KEY_OSS_DELETE, oldCourse.getCover());
        }
        // 修改课程信息
        int updateCount = baseMapper.updateById(course);
        if (updateCount == 0)
            return false;
        // 修改课程描述
        int desUpdateCount = descriptionMapper.updateById(description);
        return desUpdateCount != 0;
    }

    @Override
    public PublishCourseVo queryPublishCourse(String courseId) {
        return baseMapper.selectPublishCourse(courseId);
    }

    @Override
    public Map<String,Object> queryCourseListByPage(int current, int limit, CourseCondition condition) {
        int index = (current-1)*limit;
        List<CourseVo> courseList = baseMapper.selectCourseListByPage(index,limit,condition);
        // 查询课程的数量
        Integer total = baseMapper.selectCount(null);
        Map<String,Object> courseData = new HashMap<>();
        courseData.put("total",total);
        courseData.put("courseList",courseList);
        return courseData;
    }

    @Override
    @Transactional
    public boolean multiDeleteCourseInfo(List<String> courseIds) {
        // 获取课程的所有小节的视频ID (有多门课程)
        List<String> idList = videoMapper.selectAllVideoSourceId(courseIds);
        if (idList.size() > 0){
            //使用rabbitMq优化
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_VIDEO,ROUTING_KEY_VIDEO_DELETE_MULTI,idList);
        }
        // 删除小节
        videoMapper.deleteByCourseIds(courseIds);
        // 删除章节
        chapterMapper.deleteByCourseIds(courseIds);
        // 删除课程描述
        descriptionMapper.deleteBatchIds(courseIds);
        // 删除评论
        commentMapper.deleteByCourseIds(courseIds);
        // 批量删除课程封面
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Course::getCover);
        wrapper.in(Course::getId,courseIds);
        List<Course> courseList = baseMapper.selectList(wrapper);
        List<String> coverUrlList = courseList.stream().map(Course::getCover).collect(Collectors.toList());
        if (coverUrlList.size() > 0) {
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_OSS, ROUTING_KEY_OSS_DELETE_MULTI, coverUrlList);
        }
        // 删除课程
        int removeCount = baseMapper.deleteBatchIds(courseIds);
        return removeCount != 0;
    }

    @Override
    public Map<String, Object> queryCoursePageByCondition(Page<Course> coursePage, FrontCourseCondition condition) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!StringUtils.isEmpty(condition.getSubjectParentId()),Course::getSubjectParentId,condition.getSubjectParentId());
        wrapper.eq(!StringUtils.isEmpty(condition.getSubjectId()),Course::getSubjectId,condition.getSubjectId());
        wrapper.eq(!StringUtils.isEmpty(condition.getIsFree()),Course::getPrice,0);
        wrapper.orderByDesc(!StringUtils.isEmpty(condition.getBuyCountSort()),Course::getBuyCount);
        wrapper.orderByDesc(!StringUtils.isEmpty(condition.getGmtCreateSort()),Course::getGmtCreate);
        wrapper.orderByDesc(!StringUtils.isEmpty(condition.getPriceSort()),Course::getPrice);
        baseMapper.selectPage(coursePage,wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("size",coursePage.getSize());
        map.put("pages",coursePage.getPages());
        map.put("total",coursePage.getTotal());
        map.put("hasNext",coursePage.hasNext());
        map.put("current",coursePage.getCurrent());
        map.put("courseList",coursePage.getRecords());
        map.put("hasPrevious",coursePage.hasPrevious());
        return map;
    }

    @Override
    public Map<String, Object> queryCourseDetails(String courseId, HttpServletRequest request) {
        CourseDetailVo courseDetail = baseMapper.selectCourseDetails(courseId);
        List<ChapterVo> chapterList = chapterService.getChapterAndVideoByCourseId(courseId);
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        if ("".equals(userId)){
            // 未登录
            HashMap<String,Object> map = new HashMap<>();
            map.put("course",courseDetail);
            map.put("chapterList",chapterList);
            map.put("isBuy",false);
            return map;
        }
        if (!JwtUtils.checkToken(request))
            throw new EduException(28004,"token解析失败");
        boolean bought = orderClient.isBought(courseId, userId);
        HashMap<String,Object> map = new HashMap<>();
        map.put("course",courseDetail);
        map.put("chapterList",chapterList);
        map.put("isBuy",bought);
        return map;
    }

    @Override
    public ClientCourseVo queryCourseAndTeacherNameById(String courseId) {
        return baseMapper.selectCourseAndTeacherNameById(courseId);
    }

    @Override
    public boolean updateViewNum(String videoId) {
        if (StringUtils.isEmpty(videoId)) return false;
        Integer update = baseMapper.updateViewNumByVideoId(videoId);
        return update != null && update >= 1;
    }

    @Override
    public boolean updateBuyNum(String courseId) {
        if (StringUtils.isEmpty(courseId)) return false;
        Integer update = baseMapper.updateBuyNumById(courseId);
        return update != null && update >= 1;
    }

    @Override
    public List<Course> getIndexCourseData() {
        return baseMapper.selectIndexCourseData();
    }
}
