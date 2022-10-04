package com.why.edu.client;

import com.why.edu.client.impl.UserClientImpl;
import com.why.servicebase.entity.vo.ClientUserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Component
@FeignClient(name = "service-user",fallback = UserClientImpl.class)
public interface UserClient {

    @GetMapping("/user/service/info/{userId}")
    ClientUserInfoVo queryUserInfoById(@PathVariable("userId")String userId);
}
