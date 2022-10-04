package com.why.edu.task;

import com.why.edu.service.StatisticsDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ScheduledTask {

    @Autowired
    private StatisticsDailyService dailyService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void createStatisticsTask(){
        // 获取前一天的时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long yesterday = System.currentTimeMillis() - (24*60*60*1000);
        String yesterdayStr = format.format(new Date(yesterday));
        dailyService.saveStatisticsByDate(yesterdayStr);

    }
}
