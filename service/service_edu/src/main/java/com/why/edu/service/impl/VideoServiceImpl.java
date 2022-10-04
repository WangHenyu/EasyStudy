package com.why.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.edu.entity.Video;
import com.why.edu.entity.vo.VideoVo;
import com.why.edu.mapper.VideoMapper;
import com.why.edu.service.VideoService;
import com.why.servicebase.entity.vo.ClientCourseVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.why.commonconst.RabbitConst.*;



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
    private RabbitTemplate rabbitTemplate;

    @Override
    public boolean removeVideoAndSource(String videoId) {

        // 查询视频ID
        String videoSourceId = baseMapper.selectVideoSourceId(videoId);
        if (!StringUtils.isEmpty(videoSourceId)){
            // 删除阿里云上的视频（异步）
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_VIDEO,ROUTING_KEY_VIDEO_DELETE,videoSourceId);
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
            // rabbitMq优化（异步）
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_VIDEO,ROUTING_KEY_VIDEO_DELETE,videoSourceId);
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
