package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.why.edu.client.CourseClient;
import com.why.edu.client.OrderClient;
import com.why.edu.client.UserClient;
import com.why.edu.client.VodClient;
import com.why.edu.entity.StatisticsDaily;
import com.why.edu.entity.condition.ChartCondition;
import com.why.edu.mapper.StatisticsDailyMapper;
import com.why.edu.service.StatisticsDailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-29
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private CourseClient courseClient;
    @Autowired
    private VodClient vodClient;
    @Autowired
    private OrderClient orderClient;

    @Override
    @CacheEvict(value = "statistics", allEntries=true)
    public void saveStatisticsByDate(String date) {
        // 删除已存在的统计记录
        LambdaQueryWrapper<StatisticsDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StatisticsDaily::getDateCalculated,date);
        baseMapper.delete(wrapper);
        // 获取统计信息
        int registerCount = userClient.queryCountByDate(date);
        int loginCount = userClient.queryLoginCount(date);
        int commentCount = courseClient.queryCountByDate(date);
        int vodCount = vodClient.queryViewCountDaily(date);
        Map<String, Object> orderStatistics = orderClient.queryPayCountAndAmountDaily(date);
        Double fee = (Double) orderStatistics.get("fee");
        Integer buyNum = (Integer)orderStatistics.get("buyNum");

        // 创建统计对象
        StatisticsDaily statisticsDaily = new StatisticsDaily();
        statisticsDaily.setDateCalculated(date);
        statisticsDaily.setRegisterNum(registerCount);
        statisticsDaily.setCommentNum(commentCount);
        statisticsDaily.setVideoViewNum(vodCount);
        statisticsDaily.setTotalFee(fee);
        statisticsDaily.setBuyNum(buyNum);
        statisticsDaily.setLoginNum(loginCount);
        // 添加统计数据
        baseMapper.insert(statisticsDaily);
        return;
    }

    @Override
    public Map<String, Object> getChartsInfo(ChartCondition condition) {
        String begin = condition.getBegin();
        String end = condition.getEnd();
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.select(condition.getType(), "date_calculated");
        if (!StringUtils.isEmpty(begin)){
            wrapper.ge("date(date_calculated)",begin);
        }
        if (!StringUtils.isEmpty(end)){
            wrapper.le("date(date_calculated)",end);
        }
        List<StatisticsDaily> statisticsList = baseMapper.selectList(wrapper);
        List dataList = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        for (StatisticsDaily statisticsDaily : statisticsList) {
            dateList.add(statisticsDaily.getDateCalculated());
            switch (condition.getType()){
                case "register_num":
                    dataList.add(statisticsDaily.getRegisterNum());
                    break;
                case "login_num":
                    dataList.add(statisticsDaily.getLoginNum());
                    break;
                case "video_view_num":
                    dataList.add(statisticsDaily.getVideoViewNum());
                    break;
                case "comment_num":
                    dataList.add(statisticsDaily.getCommentNum());
                    break;
                case "buy_num":
                    dataList.add(statisticsDaily.getBuyNum());
                    break;
                case "total_fee":
                    dataList.add(statisticsDaily.getTotalFee());
                    break;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("dataList",dataList);
        return map;
    }

    @Override
    @Cacheable(value = "statistics",key = "'statisticsData'")
    public Map<String, Object> queryLastSevenDayData() {
        // 获取当前时间和一周前的日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long millis = System.currentTimeMillis();
        long lastWeekMillis = millis - (7 * 24 * 60 * 60 * 1000);
        Date now = new Date(millis);
        Date lastWeek = new Date(lastWeekMillis);
        String nowStr = simpleDateFormat.format(now);
        String lastWeekStr = simpleDateFormat.format(lastWeek);
        LambdaQueryWrapper<StatisticsDaily> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(StatisticsDaily::getDateCalculated,lastWeekStr,nowStr);
        List<StatisticsDaily> statisticsList = baseMapper.selectList(wrapper);
        List<String> dateList = new ArrayList<>();
        List<Integer> buyNumList = new ArrayList<>();
        List<Integer> viewNumList = new ArrayList<>();
        List<Integer> loginNumList = new ArrayList<>();
        List<Integer> registerList = new ArrayList<>();
        List<Integer> commentNumList = new ArrayList<>();
        List<Double> totalFeeList = new ArrayList<>();
        for (StatisticsDaily statisticsDaily : statisticsList) {
            dateList.add(statisticsDaily.getDateCalculated());
            buyNumList.add(statisticsDaily.getBuyNum());
            viewNumList.add(statisticsDaily.getVideoViewNum());
            loginNumList.add(statisticsDaily.getLoginNum());
            registerList.add(statisticsDaily.getRegisterNum());
            commentNumList.add(statisticsDaily.getCommentNum());
            totalFeeList.add(statisticsDaily.getTotalFee());
        }
        // 课程分类占比数据
        List<Map<String, Object>> subjectDataList = courseClient.querySubjectData();
        Map<String,Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("buyNumList",buyNumList);
        map.put("viewNumList",viewNumList);
        map.put("totalFeeList",totalFeeList);
        map.put("loginNumList",loginNumList);
        map.put("registerList",registerList);
        map.put("commentNumList",commentNumList);
        map.put("subjectDataList",subjectDataList);
        return map;
    }

}
