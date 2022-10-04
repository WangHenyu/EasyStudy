package com.why.edu.controller;

import com.why.commonutils.Result;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/eduservice/user")
@Api(description = "用户登录模块")
public class UserLoginController {

    @PostMapping("login")
    public Result login(){

        return  Result.ok().data("taoke", UUID.randomUUID().toString());
    }

    @GetMapping("info")
    public Result info(){

        String avatar = "https://why-server-bucket.oss-cn-hangzhou.aliyuncs.com" +
                        "/avatar/ad0ddcff9af749ceabbb8d282cf70306-file.png";
        return Result.ok().data("roles","[admin]")
                          .data("name","admin")
                          .data("avatar",avatar);
    }
}
