package com.why.edu.service;

import com.why.edu.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.vo.VideoVo;
import com.why.servicebase.entity.vo.ClientCourseVo;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
public interface VideoService extends IService<Video> {

    boolean removeVideoAndSource(String videoId);

    boolean saveOrUpdateVideo(VideoVo videoVo);

    ClientCourseVo getCourseInfoByVideoId(String videoId);
}
