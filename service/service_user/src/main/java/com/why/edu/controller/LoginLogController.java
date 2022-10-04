package com.why.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.Result;
import com.why.edu.entity.LoginLog;
import com.why.edu.entity.condition.LogCondition;
import com.why.edu.service.LoginLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/service")
@Api(tags = "登录日志模块")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    @PostMapping("/login/log/{current}/{limit}")
    @ApiOperation("条件分页查询登录记录")
    public Result queryLoginLogByPage(@PathVariable("current")int current,
                                      @PathVariable("limit") int limit,
                                      @RequestBody LogCondition condition){
        Page<LoginLog> logPage = new Page<>(current,limit);
        Map<String,Object> map = loginLogService.getLoginLogByPage(logPage,condition);
        return Result.ok().data(map);
    }

    @DeleteMapping("/delete/multi")
    @ApiOperation("批量删除")
    public Result deleteMulti(@RequestBody List<String> ids){
        if (ids.size() == 0)
            return Result.error().message("尚未选择");
        loginLogService.removeByIds(ids);
        return Result.ok();
    }

    @GetMapping("/log/{date}")
    @ApiOperation("根据日期获取当天登录人数")
    public int queryLoginCount(@PathVariable("date")String date){
        QueryWrapper<LoginLog> wrapper = new QueryWrapper<>();
        wrapper.eq("date(gmt_create)",date);
        int count = loginLogService.count(wrapper);
        return count;
    }
}
