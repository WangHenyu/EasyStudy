package com.why.edu.client;

import com.why.servicebase.entity.vo.ClientCourseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "service-edu")
public interface CourseClient {

    @GetMapping("/eduservice/course/client/{courseId}")
    ClientCourseVo queryClientCourseInfoById(@PathVariable("courseId")String courseId);

    @GetMapping("/eduservice/course/update/buy/num/{courseId}")
    boolean updateBuyNum(@PathVariable("courseId") String courseId);
}
