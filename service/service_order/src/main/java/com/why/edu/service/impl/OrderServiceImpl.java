package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.client.CourseClient;
import com.why.edu.client.UserClient;
import com.why.edu.entity.Order;
import com.why.edu.entity.condition.OrderCondition;
import com.why.edu.mapper.OrderMapper;
import com.why.edu.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.edu.utils.OrderUtils;
import com.why.servicebase.entity.vo.ClientCourseVo;
import com.why.servicebase.entity.vo.ClientUserInfoVo;
import com.why.servicebase.exception.EduException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CourseClient courseClient;
    @Autowired
    private UserClient userClient;

    @Override
    public String createOrder(String courseId, String userId) {
        // 若订单存在就直接返回订单号(每个用户只能下一单)
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getCourseId,courseId)
                .eq(Order::getUserId,userId)
                .eq(Order::getStatus,0);
        Order order = baseMapper.selectOne(wrapper);
        if (order != null){
            return order.getOrderNo();
        }
        // 获取课程信息
        ClientCourseVo courseInfo = courseClient.queryClientCourseInfoById(courseId);
        if (courseInfo==null)
            throw new EduException(20001,"生成订单失败：课程不存在");
        // 获取用户信息
        ClientUserInfoVo userInfo = userClient.queryUserInfoById(userId);
        if (userInfo==null)
            throw new EduException(20001,"生成订单失败：用户不存在");
        order = new Order();
        order.setOrderNo(OrderUtils.createOrderNo());
        order.setCourseId(courseId);
        order.setCourseTitle(courseInfo.getTitle());
        order.setCourseCover(courseInfo.getCover());
        order.setTeacherName(courseInfo.getTeacherName());
        order.setUserId(userInfo.getId());
        order.setNickname(userInfo.getNickname());
        order.setMobile(userInfo.getMobile());
        order.setTotalFee(courseInfo.getPrice());
        order.setPayType(1); //1微信支付 2支付宝支付
        order.setStatus(0); //0未支付 1已支付
        // 添加订单
        int insert = baseMapper.insert(order);
        if (insert == 0)
            throw new EduException(20001,"创建订单失败");
        return order.getOrderNo();
    }


    @Override
    public Map<String, Object> queryOrderByCondition(Page page, OrderCondition condition) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Order::getCourseId,
                       Order::getOrderNo,
                       Order::getNickname,
                       Order::getTeacherName,
                       Order::getCourseTitle,
                       Order::getTotalFee,
                       Order::getMobile,
                       Order::getPayType,
                       Order::getStatus,
                       Order::getGmtCreate);
        wrapper.eq(!Strings.isEmpty(condition.getOrderNo()),Order::getOrderNo,condition.getOrderNo());
        wrapper.like(!Strings.isEmpty(condition.getCourseName()),Order::getCourseTitle,condition.getCourseName());
        wrapper.like(!Strings.isEmpty(condition.getNickname()),Order::getNickname,condition.getNickname());
        wrapper.ge(!Strings.isEmpty(condition.getBegin()),Order::getGmtCreate,condition.getBegin());
        if (!Strings.isEmpty(condition.getEnd())){
            condition.setEnd(condition.getEnd()+" 23:59:59");
            wrapper.le(Order::getGmtCreate,condition.getEnd());
        }
        wrapper.orderByDesc(Order::getGmtCreate);
        baseMapper.selectPage(page,wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("orderList",page.getRecords());
        return map;
    }

    @Override
    public boolean updateCourseBuyNum(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo,orderNo);
        Order order = baseMapper.selectOne(wrapper);
        if (StringUtils.isEmpty(order.getCourseId())) return false;
        courseClient.updateBuyNum(order.getCourseId());
        return true;
    }
}
