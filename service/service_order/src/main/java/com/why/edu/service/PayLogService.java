package com.why.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.PayLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.condition.PayLogCondition;

import java.util.Map;

/**
 * <p>
 * 支付日志表 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-26
 */
public interface PayLogService extends IService<PayLog> {

    Map<String, Object> createNative(String orderNo);

    Map<String, String> queryPayStatus(String orderNo);

    void updateOrderStatus(Map<String, String> map);

    Map<String, Object> queryPayLogByPageCondition(Page<PayLog> payLogPage, PayLogCondition condition);

    Map<String, Object> getPayCountAndAmountDaily(String date);

}
