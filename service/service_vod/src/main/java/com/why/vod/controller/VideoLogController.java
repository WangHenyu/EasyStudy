package com.why.vod.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.JwtUtils;
import com.why.commonutils.Result;
import com.why.vod.entity.VideoLog;
import com.why.vod.entity.condition.LogCondition;
import com.why.vod.service.VideoLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Api(description = "视频播放记录")
@RequestMapping("/eduvod/log")
public class VideoLogController {

    @Autowired
    private VideoLogService videoLogService;

    @PostMapping("/page/{current}/{limit}")
    @ApiOperation("条件分页查询播放记录")
    public Result queryVideoLogByPage(@PathVariable("current")int current,
                                      @PathVariable("limit")int limit,
                                      @RequestBody LogCondition condition){
        Page<VideoLog> logPage = new Page<>(current, limit);
        Map<String,Object> map = videoLogService.getVideoLogByPage(logPage, condition);
        return Result.ok().data(map);
    }

    @GetMapping("/auth/{current}/{limit}")
    @ApiOperation("根据用户Id")
    public Result queryVideoLogByUserId(@PathVariable("current") int current,
                                        @PathVariable("limit") int limit,
                                        HttpServletRequest request){
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        Page<VideoLog> logPage = new Page<>(current,limit);
        LambdaQueryWrapper<VideoLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoLog::getUserId,userId);
        wrapper.orderByDesc(VideoLog::getPlayDate);
        videoLogService.page(logPage,wrapper);
        return Result.ok().data("total",logPage.getTotal()).data("list",logPage.getRecords());
    }

    @DeleteMapping("/delete/multi")
    @ApiOperation("批量删除")
    public Result deleteMulti(@RequestBody List<String> ids){
        if (ids.size() == 0)
            return Result.error().message("尚未选择");
        videoLogService.removeByIds(ids);
        return Result.ok();
    }

    @GetMapping("/view/count/{date}")
    @ApiOperation("根据日期获取当天的视频播放量")
    public int queryViewCountDaily(@PathVariable("date")String date){
        int count = videoLogService.getViewCountDaily(date);
        return count;
    }
}
