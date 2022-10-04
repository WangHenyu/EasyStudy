package com.why.edu.service;

import com.why.edu.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.vo.ChapterVo;

import java.util.List;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-12
 */
public interface ChapterService extends IService<Chapter> {

    List<ChapterVo> getChapterAndVideoByCourseId(String courseId);

    boolean deleteChapterById(String chapterId);

}
