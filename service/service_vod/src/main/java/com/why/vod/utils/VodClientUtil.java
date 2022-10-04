package com.why.vod.utils;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import static com.why.vod.utils.VodConstUtil.ACCESS_KEY_ID;
import static com.why.vod.utils.VodConstUtil.ACCESS_KEY_SECRET;

public class VodClientUtil {

    private static final String REGION_ID = "cn-shanghai";

    public static DefaultAcsClient getClient(){
        //点播服务接入区(固定值)
        String regionId = REGION_ID;
        String accessKeyId = ACCESS_KEY_ID;
        String accessKeySecret = ACCESS_KEY_SECRET;
        //获取配置
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        DefaultAcsClient defaultAcsClient = new DefaultAcsClient(profile);
        return defaultAcsClient;
    }
}
