package com.why.vod.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.JwtUtils;
import com.why.servicebase.entity.vo.ClientCourseVo;
import com.why.servicebase.exception.EduException;
import com.why.vod.client.CourseClient;
import com.why.vod.entity.VideoLog;
import com.why.vod.entity.condition.LogCondition;
import com.why.vod.mapper.VideoLogMapper;
import com.why.vod.service.VideoLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-29
 */
@Service
public class VideoLogServiceImpl extends ServiceImpl<VideoLogMapper, VideoLog> implements VideoLogService {

    @Autowired
    private CourseClient courseClient;

    @Override
    public void saveOrUpdateVideoLog(HttpServletRequest request, String videoId, String videoSourceId) {
        // 只要有人看就更新播放量（无论是否登录）
        courseClient.updateViewNum(videoId);
        if (!JwtUtils.checkToken(request)) return;
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        String nickname = JwtUtils.getNickNameWithJwtToken(request);
        // 未登录就不保存播放记录
        if (StringUtils.isEmpty(userId)) return;
        // 判断是否观看过
        LambdaQueryWrapper<VideoLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoLog::getUserId,userId).eq(VideoLog::getVideoId,videoId);
        VideoLog videoLog = baseMapper.selectOne(wrapper);
        if (!Objects.isNull(videoLog)){
            // 观看过就修改观看时间
            videoLog.setPlayDate(new Date());
            baseMapper.updateById(videoLog);
            return;
        }
        // 获取课程信息
        ClientCourseVo courseInfo = courseClient.queryCourseInfo(videoId);
        videoLog = new VideoLog();
        videoLog.setUserId(userId);
        videoLog.setVideoId(videoId);
        videoLog.setNickname(nickname);
        videoLog.setPlayDate(new Date());
        videoLog.setVideoSourceId(videoSourceId);
        videoLog.setCourseTitle(courseInfo.getTitle());
        videoLog.setCourseCover(courseInfo.getCover());
        videoLog.setVideoTitle(courseInfo.getVideoTitle());
        baseMapper.insert(videoLog);
        return;
    }

    @Override
    public int getViewCountDaily(String date) {
        QueryWrapper<VideoLog> wrapper = new QueryWrapper<>();
        wrapper.eq("date(play_date)",date);
        Integer count = baseMapper.selectCount(wrapper);
        return count;
    }

    @Override
    public Map<String, Object> getVideoLogByPage(Page<VideoLog> logPage, LogCondition condition) {
        if (!StringUtils.isEmpty(condition.getEnd()))
            condition.setEnd(condition.getEnd()+" 23:59:59");
        LambdaQueryWrapper<VideoLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(condition.getNickname()),VideoLog::getNickname,condition.getNickname());
        wrapper.like(!StringUtils.isEmpty(condition.getCourseTitle()),VideoLog::getCourseTitle,condition.getCourseTitle());
        wrapper.like(!StringUtils.isEmpty(condition.getVideoTitle()),VideoLog::getVideoTitle,condition.getVideoTitle());
        wrapper.ge(!StringUtils.isEmpty(condition.getBegin()),VideoLog::getPlayDate,condition.getBegin());
        wrapper.le(!StringUtils.isEmpty(condition.getEnd()),VideoLog::getPlayDate,condition.getEnd());
        wrapper.orderByDesc(VideoLog::getPlayDate);
        baseMapper.selectPage(logPage,wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("total",logPage.getTotal());
        map.put("list",logPage.getRecords());
        return map;
    }
}
