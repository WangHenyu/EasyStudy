package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.why.commonutils.JwtUtils;
import com.why.edu.entity.User;
import com.why.edu.service.LoginLogService;
import com.why.edu.service.LoginService;
import com.why.edu.service.UserService;
import com.why.edu.utils.HttpClientUtils;
import com.why.edu.utils.IpUtils;
import com.why.edu.utils.WxConstUtils;
import com.why.servicebase.exception.EduException;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginLogService loginLogService;

    @Override
    public String getWxCodeUrl() {
        // 微信规定的地址（获取临时票据code）
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        String appId = WxConstUtils.APP_ID;
        String redirectUrl = WxConstUtils.REDIRECT_URL;
        String state = "hy";

        try{
            redirectUrl =  URLEncoder.encode(redirectUrl,"UTF-8");
        }catch(Exception e){
            e.printStackTrace();
            throw new EduException(20001,"error");
        }
        String url = String.format(baseUrl, appId, redirectUrl, state);
        return url;
    }


    @Override
    public String callback(String code,String state,HttpServletRequest request) {
        try {
            // 微信规定的地址（获取access_token）
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";

            String appId = WxConstUtils.APP_ID;
            String appSecret = WxConstUtils.APP_SECRET;

            String accessTokenUrl = String.format(baseAccessTokenUrl, appId, appSecret, code);

            // 请求微信提供的地址获取token
            String jsonStr = HttpClientUtils.get(accessTokenUrl);
            // 将JSON字符串转化成Map
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap map = objectMapper.readValue(jsonStr, HashMap.class);
            String accessToken = (String) map.get("access_token");
            String openid = (String) map.get("openid");

            // 根据openId判断用户是否注册
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getOpenid, openid);
            User user = userService.getOne(wrapper);
            if (user == null) {
                // 未注册
                // 微信规定的地址（获取用户信息）
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                // 拿着accessToken和openId获取用户信息
                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
                String userInfoStr = HttpClientUtils.get(userInfoUrl);
                // 将用户信息字符串转化成Map对象
                HashMap userMap = objectMapper.readValue(userInfoStr, HashMap.class);
                // 将用户信息注册到数据库
                user = new User();
                user.setOpenid(openid);
                user.setNickname((String) userMap.get("nickname"));
                user.setSex((Integer) userMap.get("sex"));
                user.setAvatar((String) userMap.get("headimgurl"));
                boolean save = userService.save(user);
                if (!save)
                    throw new EduException(20001, "error");
            }
            // 添加登录记录
            loginLogService.saveLoginLog(user,request);
            // 生成JWT添加用户信息（传递给前端）
            String jwtToken = JwtUtils.getJwtToken(user.getId(), user.getNickname());
            // 注意？拼接
            return jwtToken;
        }catch(Exception exception){
            exception.printStackTrace();
            throw new EduException(20001,"error");
        }
    }
}
