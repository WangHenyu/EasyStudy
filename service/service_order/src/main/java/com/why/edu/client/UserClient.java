package com.why.edu.client;

import com.why.servicebase.entity.vo.ClientUserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient(name = "service-user")
public interface UserClient {

    @GetMapping("/user/service/info/{userId}")
    ClientUserInfoVo queryUserInfoById(@PathVariable("userId")String userId);
}
