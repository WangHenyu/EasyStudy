package com.why.edu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.Result;
import com.why.edu.entity.PayLog;
import com.why.edu.entity.condition.PayLogCondition;
import com.why.edu.service.OrderService;
import com.why.edu.service.PayLogService;
import com.why.servicebase.exception.EduException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-26
 */
@RestController
@RequestMapping("/eduorder/pay")
public class PayLogController {

    @Autowired
    private PayLogService payLogService;
    @Autowired
    private OrderService orderService;


    @GetMapping("/create/{orderNo}")
    @ApiOperation("根据订单号生成二维码")
    public Result createNative(@PathVariable("orderNo")String orderNo){
        Map<String,Object> map = payLogService.createNative(orderNo);
        return Result.ok().data("native",map);
    }

    @GetMapping("/query/status/{orderNo}")
    @ApiOperation("查询订单状态")
    public Result queryPayStatus(@PathVariable("orderNo")String orderNo){
        // 查询订单状态
        Map<String, String> map = payLogService.queryPayStatus(orderNo);
        if (map == null){
            return Result.error().message("支付失败");
        }
        if (map.get("trade_state").equals("SUCCESS")){
            //更改订单状态
            payLogService.updateOrderStatus(map);
            //更新课程购买数
            orderService.updateCourseBuyNum(orderNo);
            return Result.ok().message("支付成功");
        }
        return Result.ok().code(25000).message("支付中");
    }

    @PostMapping("/page/condition/{current}/{limit}")
    @ApiOperation("分页条件查询支付记录")
    public Result queryPayLogByPageCondition(@PathVariable("current")int current,
                                             @PathVariable("limit")int limit,
                                             @RequestBody PayLogCondition condition){
        Page<PayLog> payLogPage = new Page<>(current,limit);
        Map<String,Object> map = payLogService.queryPayLogByPageCondition(payLogPage,condition);
        return Result.ok().data(map);
    }

    @DeleteMapping("/delete/{payLogId}")
    @ApiOperation("删除交易记录")
    public Result deletePayLog(@PathVariable("payLogId")String payLogId){
        System.out.println(payLogId);
        boolean remove = payLogService.removeById(payLogId);
        return remove? Result.ok() : Result.error();
    }

    @GetMapping("/count/fee/{date}")
    @ApiOperation("根据日期获取当日的销售数量及成交金额")
    public Map<String,Object> queryPayCountAndAmountDaily(@PathVariable("date") String date){
        Map<String,Object> map = payLogService.getPayCountAndAmountDaily(date);
        return map;
    }
}

