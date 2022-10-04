package com.why.edu.client.impl;

import com.why.edu.client.UserClient;
import com.why.servicebase.entity.vo.ClientUserInfoVo;
import com.why.servicebase.exception.EduException;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {
    @Override
    public ClientUserInfoVo queryUserInfoById(String userId) {
        throw new EduException(20001,"获取用户信息失败");
    }
}
