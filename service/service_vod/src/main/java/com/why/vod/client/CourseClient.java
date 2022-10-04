package com.why.vod.client;

import com.why.servicebase.entity.vo.ClientCourseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name="service-edu")
public interface CourseClient {

    @GetMapping("/eduservice/video/course/info/{videoId}")
    ClientCourseVo queryCourseInfo(@PathVariable("videoId")String videoId);

    @GetMapping("/eduservice/course/update/view/num/{videoId}")
    boolean updateViewNum(@PathVariable("videoId") String videoId);

}
