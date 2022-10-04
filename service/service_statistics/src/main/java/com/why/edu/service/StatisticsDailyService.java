package com.why.edu.service;

import com.why.edu.entity.StatisticsDaily;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.condition.ChartCondition;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-29
 */
public interface StatisticsDailyService extends IService<StatisticsDaily> {

    void saveStatisticsByDate(String date);

    Map<String, Object> getChartsInfo(ChartCondition condition);

    Map<String, Object> queryLastSevenDayData();
}
