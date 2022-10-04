package com.why.edu.utils;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wxpay")
public class WxPayConstUtils implements InitializingBean {

    private String appId;
    private String partner;
    private String partnerKey;
    private String spbillCreateIp;
    private String notifyUrl;


    public static String APP_ID;
    public static String PARTNER;
    public static String PARTNER_KEY;
    public static String SPBILL_CREATE_IP;
    public static String NOTIFY_URL;

    @Override
    public void afterPropertiesSet() throws Exception {
        APP_ID = appId;
        PARTNER = partner;
        PARTNER_KEY = partnerKey;
        SPBILL_CREATE_IP =spbillCreateIp;
        NOTIFY_URL = notifyUrl;
    }
}
