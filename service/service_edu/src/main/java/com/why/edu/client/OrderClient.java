package com.why.edu.client;

import com.why.edu.client.impl.OrderClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "service-order",fallback = OrderClientImpl.class)
public interface OrderClient {

    @GetMapping("/eduorder/bought/{courseId}/{userId}")
    boolean isBought(@PathVariable("courseId") String courseId,@PathVariable("userId") String userId);
}
