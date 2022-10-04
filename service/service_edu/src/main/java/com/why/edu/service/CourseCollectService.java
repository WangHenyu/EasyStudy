package com.why.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.CourseCollect;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-10-03
 */
public interface CourseCollectService extends IService<CourseCollect> {

    Map<String, Object> getCollectionPageByUserId(int current, int limit, String userId);

}
