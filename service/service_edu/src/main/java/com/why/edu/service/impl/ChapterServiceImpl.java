package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.why.edu.entity.Chapter;
import com.why.edu.entity.Video;
import com.why.edu.entity.vo.ChapterVo;
import com.why.edu.entity.vo.VideoVo;
import com.why.edu.mapper.ChapterMapper;
import com.why.edu.mapper.VideoMapper;
import com.why.edu.service.ChapterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.edu.service.VideoService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.why.commonconst.RabbitConst.EXCHANGE_DIRECT_VIDEO;
import static com.why.commonconst.RabbitConst.ROUTING_KEY_VIDEO_DELETE_MULTI;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Override
    public List<ChapterVo> getChapterAndVideoByCourseId(String courseId) {
        // 根据课程id查询所有章节
        LambdaQueryWrapper<Chapter> chapterWrapper = new LambdaQueryWrapper<>();
        chapterWrapper.eq(Chapter::getCourseId,courseId);
        List<Chapter> chapters = baseMapper.selectList(chapterWrapper);
        // 根据课程id查询所有小节
        LambdaQueryWrapper<Video> videoWrapper = new LambdaQueryWrapper<>();
        videoWrapper.eq(Video::getCourseId,courseId);
        List<Video> videos = videoMapper.selectList(videoWrapper);
        List<ChapterVo> chapterVoList = new ArrayList<>();
        // 封装章节
        chapters.stream().forEach(chapter->{
            ChapterVo chapterVo = new ChapterVo();
            BeanUtils.copyProperties(chapter, chapterVo);
            String chapterId = chapter.getId();

            // 封装小节
            List<VideoVo> childrenList = new ArrayList<>();
            videos.stream()
                    .filter(video -> video.getChapterId().equals(chapterId))
                    .forEach(video -> {
                        VideoVo videoVo = new VideoVo();
                        BeanUtils.copyProperties(video, videoVo);
                        // 解决取消上传视频的bug
                        // videoVo.setVideoSourceId(null);
                        childrenList.add(videoVo);
            });
            chapterVo.setVideos(childrenList);
            chapterVoList.add(chapterVo);
        });
        return chapterVoList;
    }


    @Override
    @Transactional
    public boolean deleteChapterById(String chapterId) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Video::getVideoSourceId);
        wrapper.eq(Video::getChapterId, chapterId);
        List<Video> videos = videoMapper.selectList(wrapper);
        List<String> videoSourcesIds = videos.stream().map(video -> video.getVideoSourceId()).collect(Collectors.toList());
        videoMapper.delete(wrapper);
        baseMapper.deleteById(chapterId);
        // 发送删除视频消息给rabbitMq
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_VIDEO,ROUTING_KEY_VIDEO_DELETE_MULTI,videoSourcesIds);
        return true;
    }
}
