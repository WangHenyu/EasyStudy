package com.why.vod.client.fallback;

import com.why.servicebase.entity.vo.ClientCourseVo;
import com.why.servicebase.exception.EduException;
import com.why.vod.client.CourseClient;
import org.springframework.stereotype.Component;

@Component
public class CourseClientImpl implements CourseClient {

    @Override
    public ClientCourseVo queryCourseInfo(String videoId) {
        throw new EduException(20001,"获取课程信息失败");
    }

    @Override
    public boolean updateViewNum(String videoId) {
        return false;
    }

}
