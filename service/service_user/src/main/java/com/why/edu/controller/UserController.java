package com.why.edu.controller;


import com.why.commonutils.JwtUtils;
import com.why.commonutils.Result;
import com.why.edu.entity.User;
import com.why.edu.entity.vo.LoginVo;
import com.why.edu.entity.vo.PasswordVo;
import com.why.edu.entity.vo.RegisterVo;
import com.why.edu.entity.vo.UserInfoVo;
import com.why.edu.service.LoginLogService;
import com.why.edu.service.LoginService;
import com.why.edu.service.UserService;
import com.why.servicebase.entity.vo.ClientUserInfoVo;
import com.why.servicebase.exception.EduException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-21
 */
@RestController
@RequestMapping("/user/service")
@Api(tags = "用户管理模块")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ApiOperation("前台用户登录")
    public Result login(@RequestBody LoginVo loginVo,HttpServletRequest request){
        String token = userService.login(loginVo,request);
        return Result.ok().data("token",token);
    }

    @PostMapping("/register")
    @ApiOperation("前台用户注册")
    public Result register(@RequestBody RegisterVo registerVo){
        boolean register = userService.register(registerVo);
        return register ? Result.ok() : Result.error();
    }

    @GetMapping("/auth/info")
    @ApiOperation("根据JWT中的ID获取用户信息")
    public Result queryUserInfo(HttpServletRequest request){
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        UserInfoVo userInfoVo = userService.queryUserInfo(userId);
        return Result.ok().data("userInfo",userInfoVo);
    }

    @GetMapping("/auth/detail")
    @ApiOperation("根据JWT中的ID获取用户信息")
    public Result queryUserDetail(HttpServletRequest request){
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        User user = userService.getById(userId);
        return Result.ok().data("user",user);
    }

    @PostMapping("/auth/update")
    @ApiOperation("修改密码")
    public Result updatePassword(@RequestBody PasswordVo passwordVo,HttpServletRequest request){
        boolean success = userService.updatePassword(passwordVo, request);
        return success ? Result.ok() : Result.error();
    }
    @GetMapping("/info/{userId}")
    @ApiOperation("根据用户Id获取用户信息")
    public ClientUserInfoVo queryUserInfoById(@PathVariable("userId")String userId){
        User user = userService.getById(userId);
        ClientUserInfoVo clientUserInfoVo = new ClientUserInfoVo();
        BeanUtils.copyProperties(user,clientUserInfoVo);
        return clientUserInfoVo;
    }

    @GetMapping("/statistics/register/{date}")
    @ApiOperation("根据日期统计当天的注册人数")
    public int queryCountByDate(@PathVariable("date")String date){
        int count = userService.getCountByDate(date);
        return count;
    }

    @PostMapping("/update")
    @ApiOperation("修改用户信息")
    public Result updateUser(@RequestBody User user){
        boolean success = userService.updateUser(user);
        return success ? Result.ok() : Result.error();
    }
}

