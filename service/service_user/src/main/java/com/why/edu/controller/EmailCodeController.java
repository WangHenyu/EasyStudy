package com.why.edu.controller;

import com.why.commonutils.Result;
import com.why.edu.service.EmailCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/code")
@Api(tags = "获取邮箱验证码")
public class EmailCodeController {

    @Autowired
    private EmailCodeService emailCodeService;

    @GetMapping("/send/{email}")
    @ApiOperation("发送邮箱验证吗")
    public Result code(@PathVariable("email")String email){

        boolean success = emailCodeService.sendCode(email);
        return success ? Result.ok() : Result.error();
    }

}
