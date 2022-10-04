package com.why.edu.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "service-user")
public interface UserClient {

    @GetMapping("/user/service/statistics/register/{date}")
    int queryCountByDate(@PathVariable("date")String date);

    @GetMapping("/user/service/log/{date}")
    int queryLoginCount(@PathVariable("date")String date);
}
