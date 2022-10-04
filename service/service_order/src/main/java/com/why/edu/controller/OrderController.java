package com.why.edu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.JwtUtils;
import com.why.commonutils.Result;
import com.why.edu.entity.Order;
import com.why.edu.entity.condition.OrderCondition;
import com.why.edu.service.OrderService;
import com.why.servicebase.exception.EduException;
import io.jsonwebtoken.JwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-26
 */
@RestController
@RequestMapping("/eduorder")
@Api(description = "订单管理")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/save/{courseId}")
    @ApiOperation("生成订单")
    public Result saveOrder(@PathVariable("courseId")String courseId, HttpServletRequest request){
        //token的判断已经在网关实现
        //if (!JwtUtils.checkToken(request)) throw new EduException(28004,"失效的token");
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        //if ("".equals(userId)) throw new EduException(28004,"用户未登录");
        String orderNo = orderService.createOrder(courseId,userId);
        return Result.ok().data("orderNo",orderNo);
    }

    @GetMapping("/query/{orderNo}")
    @ApiOperation("根据订单号查询订单信息")
    public Result queryOrderByNo(@PathVariable("orderNo") String orderNo){
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo,orderNo);
        Order order = orderService.getOne(wrapper);
        return Result.ok().data("order",order);
    }

    @GetMapping("/page/auth/{current}/{limit}")
    @ApiOperation("根据用户Id查询订单信息")
    public Result queryOrderByUserId(@PathVariable("current") int current,
                                     @PathVariable("limit") int limit,
                                     HttpServletRequest request){
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        Page<Order> orderPage = new Page<>(current,limit);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId,userId);
        wrapper.orderByDesc(Order::getGmtCreate);
        orderService.page(orderPage,wrapper);
        return Result.ok().data("total",orderPage.getTotal()).data("orderList",orderPage.getRecords());
    }


    @DeleteMapping("/{orderId}")
    @ApiOperation("删除订单")
    public Result deleteOrder(@PathVariable("orderId")String orderId){
        boolean remove = orderService.removeById(orderId);
        return remove ? Result.ok() : Result.error();
    }

    @PutMapping("/update/{orderNo}/{status}")
    @ApiOperation("修改订单状态")
    public Result updateStatus(@PathVariable("orderNo") String orderNo,
                               @PathVariable("status") Integer status){
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status",status).eq("order_no",orderNo);
        boolean update = orderService.update(new Order(),updateWrapper);
        return update ? Result.ok() : Result.error();
    }

    @GetMapping("/bought/{courseId}/{userId}")
    @ApiOperation("查询课程是否被用户购买过")
    public boolean isBought(@PathVariable("courseId") String courseId,
                            @PathVariable("userId") String userId){
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getCourseId,courseId)
                .eq(Order::getUserId,userId)
                .eq(Order::getStatus,1);
        int count = orderService.count(wrapper);
        return count>0;
    }

    @PostMapping("/page/condition/{current}/{limit}")
    @ApiOperation("条件分页查询订单")
    public Result queryOrderConditionPage(@PathVariable("current") int current,
                                          @PathVariable("limit") int limit,
                                          @RequestBody OrderCondition condition){
        Page<Order> orderPage = new Page<>(current,limit);
        Map<String,Object> map = orderService.queryOrderByCondition(orderPage, condition);
        return Result.ok().data(map);
    }

}

