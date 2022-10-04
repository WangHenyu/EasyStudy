package com.why.edu.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Component
@FeignClient(name = "service-edu")
public interface CourseClient {

    @GetMapping("/eduservice/comment/daily/count/{date}")
    int queryCountByDate(@PathVariable("date")String date);

    @GetMapping("/eduservice/subject/statistics/data")
    List<Map<String,Object>> querySubjectData();
}
