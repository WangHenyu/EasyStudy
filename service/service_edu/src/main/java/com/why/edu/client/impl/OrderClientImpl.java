package com.why.edu.client.impl;

import com.why.edu.client.OrderClient;
import com.why.servicebase.exception.EduException;
import org.springframework.stereotype.Component;

@Component
public class OrderClientImpl implements OrderClient {

    @Override
    public boolean isBought(String courseId, String userId) {
        // 调用不到远程服务就返回false
        return false;
    }
}
