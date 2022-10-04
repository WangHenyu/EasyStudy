package com.why.edu.mapper;

import com.why.edu.entity.PayLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Map;

/**
 * <p>
 * 支付日志表 Mapper 接口
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-26
 */
public interface PayLogMapper extends BaseMapper<PayLog> {

    Map selectPayCountAndAmount(String date);
}
