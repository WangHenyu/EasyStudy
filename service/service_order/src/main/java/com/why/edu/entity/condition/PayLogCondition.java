package com.why.edu.entity.condition;

import lombok.Data;

@Data
public class PayLogCondition {

    private String orderNo;

    // 支付类型1微信2支付宝
    private Integer payType;

    private String begin;

    private String end;
}
