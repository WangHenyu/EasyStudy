package com.why.edu.controller;

import com.why.edu.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/ucenter/wx")
@Api(tags = "微信登录")
public class WxLoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    @ApiOperation("微信二维码登录接口")
    public String getWxCode(){

        String url = loginService.getWxCodeUrl();
        return "redirect:"+url;
    }

    @GetMapping("/callback")
    @ApiOperation("二维码")
    public String callback(String code, String state, HttpServletRequest request){

        String jwtToken = loginService.callback(code,state,request);
        return "redirect:http://localhost:3000?token="+jwtToken;
    }
}
