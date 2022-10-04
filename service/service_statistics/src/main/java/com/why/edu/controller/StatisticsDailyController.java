package com.why.edu.controller;


import com.why.commonutils.Result;
import com.why.edu.entity.condition.ChartCondition;
import com.why.edu.service.StatisticsDailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-29
 */
@RestController
@RequestMapping("/edustatistics/daily")
@Api(description = "统计分析模块")
public class StatisticsDailyController {

    @Autowired
    private StatisticsDailyService statisticsDailyService;

    @GetMapping("/{date}")
    @ApiOperation("手动生成统计数据")
    public Result createStatisticsByDate(@PathVariable("date")String date){
        statisticsDailyService.saveStatisticsByDate(date);
        return Result.ok();
    }

    @PostMapping("/chart")
    @ApiOperation("获取图表信息")
    public Result queryCharts(@RequestBody ChartCondition condition){
        Map<String, Object> map = statisticsDailyService.getChartsInfo(condition);
        return Result.ok().data(map);
    }

    @GetMapping("/seven/chart")
    @ApiOperation("获取近期七天的统计数据")
    public Result queryLastSevenDayData(){
        Map<String, Object> map = statisticsDailyService.queryLastSevenDayData();
        return Result.ok().data("chartData",map);
    }

}

