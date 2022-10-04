package com.why.edu.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "vod-service")
public interface VodClient {

    @GetMapping("/eduvod/log/view/count/{date}")
    int queryViewCountDaily(@PathVariable("date")String date);
}
