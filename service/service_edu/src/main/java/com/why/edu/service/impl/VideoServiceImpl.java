package com.why.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.commonutils.Result;
import com.why.edu.client.VodClient;
import com.why.edu.entity.Video;
import com.why.edu.entity.vo.VideoVo;
import com.why.edu.mapper.VideoMapper;
import com.why.edu.service.VideoService;
import com.why.servicebase.entity.vo.ClientCourseVo;
import com.why.servicebase.exception.EduException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private VodClient vodClient;

    @Override
    public boolean removeVideoAndSource(String videoId) {

        // 查询视频ID
        String videoSourceId = baseMapper.selectVideoSourceId(videoId);
        if (!StringUtils.isEmpty(videoSourceId)){
            // TODO rabbitMq优化
            // 删除阿里云上的视频
            Result result = vodClient.deleteVideo(videoSourceId);
            if (result.getCode() == 20001)
                throw new EduException(20001,"原视频删除失败");
        }
        // 删除小节
        int count = baseMapper.deleteById(videoId);
        return count > 0;
    }

    @Override
    public boolean saveOrUpdateVideo(VideoVo videoVo) {

        Video video = new Video();
        BeanUtils.copyProperties(videoVo,video);
        if (!StringUtils.isEmpty(videoVo.getVideoSourceId()))
            video.setStatus("Normal");
        if (StringUtils.isEmpty(video.getId())) {
            // 新增
            int insert = baseMapper.insert(video);
            return insert > 0;
        }
        // 获取旧的视频ID
        String videoSourceId = baseMapper.selectVideoSourceId(video.getId());
        if (!StringUtils.isEmpty(videoVo.getVideoSourceId())&&!StringUtils.isEmpty(videoSourceId)){
            // TODO rabbitMq优化
            // 删除旧的视频
            Result result = vodClient.deleteVideo(videoSourceId);
            if (result.getCode() == 20001){
                throw new EduException(20001,"原视频删除失败");
            }
        }
        int update = baseMapper.updateById(video);
        return update > 0;
    }

    @Override
    public ClientCourseVo getCourseInfoByVideoId(String videoId) {
        ClientCourseVo courseInfo = baseMapper.selectCourseByVideoId(videoId);
        return courseInfo;
    }
}
