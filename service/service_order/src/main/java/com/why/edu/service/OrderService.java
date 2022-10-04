package com.why.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.condition.OrderCondition;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-26
 */
public interface OrderService extends IService<Order> {

    String createOrder(String courseId, String userId);

    Map<String,Object> queryOrderByCondition(Page page, OrderCondition condition);

    boolean updateCourseBuyNum(String orderNo);
}
