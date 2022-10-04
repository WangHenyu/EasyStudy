package com.why.vod.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.vod.entity.VideoLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.vod.entity.condition.LogCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-29
 */
public interface VideoLogService extends IService<VideoLog> {

    void saveOrUpdateVideoLog(HttpServletRequest request, String videoId, String videoSourceId);

    int getViewCountDaily(String date);

    Map<String, Object> getVideoLogByPage(Page<VideoLog> logPage, LogCondition condition);
}
