package com.why.edu.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Component
@FeignClient(name = "service-order")
public interface OrderClient {

    @GetMapping("/eduorder/pay/count/fee/{date}")
    Map<String,Object> queryPayCountAndAmountDaily(@PathVariable("date") String date);
}
