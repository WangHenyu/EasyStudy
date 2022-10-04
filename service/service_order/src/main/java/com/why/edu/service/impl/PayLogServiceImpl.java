package com.why.edu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.why.edu.client.CourseClient;
import com.why.edu.entity.Order;
import com.why.edu.entity.PayLog;
import com.why.edu.entity.condition.PayLogCondition;
import com.why.edu.mapper.PayLogMapper;
import com.why.edu.service.OrderService;
import com.why.edu.service.PayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.edu.utils.HttpClient;
import com.why.edu.utils.WxPayConstUtils;
import com.why.servicebase.exception.EduException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-26
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Autowired
    private OrderService orderService;

    /**
     * 生成二维码
     */
    @Override
    public Map<String, Object> createNative(String orderNo) {
        try{
            // 获取订单信息
            LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Order::getOrderNo,orderNo);
            Order order = orderService.getOne(wrapper);
            // 设置生成二维码的参数(Key是微信规定的)
            Map<String, String> params = new HashMap<>();
            params.put("trade_type", "NATIVE");// 交易类型
            params.put("out_trade_no", orderNo);
            params.put("body", order.getCourseTitle());
            params.put("appid", WxPayConstUtils.APP_ID);
            params.put("mch_id", WxPayConstUtils.PARTNER); // 商户ID
            params.put("notify_url", WxPayConstUtils.NOTIFY_URL);
            params.put("nonce_str", WXPayUtil.generateNonceStr()); // 随机字符串
            params.put("spbill_create_ip", WxPayConstUtils.SPBILL_CREATE_IP); // 终端IP
            params.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");
            // 添加请求参数
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            // 生成数字签名（加密）
            client.setXmlParam(WXPayUtil.generateSignedXml(params,WxPayConstUtils.PARTNER_KEY));
            client.setHttps(true);
            // 请求微信二维码接口
            client.post();
            // 封装返回结果
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            Map<String,Object> map = new HashMap<>();
            map.put("orderNo", orderNo);
            map.put("courseId", order.getCourseId());
            map.put("totalFee", order.getTotalFee());
            map.put("resultCode", resultMap.get("result_code"));// 响应状态码
            map.put("codeUrl", resultMap.get("code_url")); //二维码地址
            return map;
        }catch(Exception exception){
            exception.printStackTrace();
            throw new EduException(20001,"获取二维码失败");
        }
    }

    /**
     * 查询订单状态
     */
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try{
            // 封装参数
            Map<String,String> params = new HashMap<>();
            params.put("appid", WxPayConstUtils.APP_ID);
            params.put("mch_id", WxPayConstUtils.PARTNER);
            params.put("out_trade_no", orderNo);
            params.put("nonce_str", WXPayUtil.generateNonceStr());
            // 设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(params,WxPayConstUtils.PARTNER_KEY));
            client.setHttps(true);
            client.post();
            // 得到微信返回的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            return resultMap;
        }catch(Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 更新订单状态
     */
    @Override
    public void updateOrderStatus(Map<String, String> map) {
        // 获取订单Id
        String orderNo = map.get("out_trade_no");
        // 查询订单信息
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo,orderNo);
        Order order = orderService.getOne(wrapper);
        // 订单已经支付
        if(order.getStatus() == 1) return;
        // 订单未支付,修改订单状态
        order.setStatus(1);
        orderService.updateById(order);
        //记录支付日志
        PayLog payLog=new PayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型(1微信2支付宝)
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));
        payLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(payLog);
    }

    @Override
    public Map<String, Object> queryPayLogByPageCondition(Page<PayLog> payLogPage, PayLogCondition condition) {
        LambdaQueryWrapper<PayLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(PayLog::getId,
                       PayLog::getOrderNo,
                       PayLog::getPayTime,
                       PayLog::getPayType,
                       PayLog::getTotalFee,
                       PayLog::getTradeState,
                       PayLog::getTransactionId);
        wrapper.eq(!StringUtils.isEmpty(condition.getOrderNo()),PayLog::getOrderNo,condition.getOrderNo());
        wrapper.eq(!Objects.isNull(condition.getPayType()),PayLog::getPayType,condition.getPayType());
        wrapper.ge(!StringUtils.isEmpty(condition.getBegin()),PayLog::getPayTime,condition.getBegin());
        if (!StringUtils.isEmpty(condition.getEnd())){
            condition.setEnd(condition.getEnd()+" 23:59:59");
            wrapper.le(PayLog::getPayTime,condition.getEnd());
        }
        wrapper.orderByDesc(PayLog::getPayTime);
        baseMapper.selectPage(payLogPage,wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("total",payLogPage.getTotal());
        map.put("payLogList",payLogPage.getRecords());
        return map;
    }

    @Override
    public Map<String, Object> getPayCountAndAmountDaily(String date) {
        Map map = baseMapper.selectPayCountAndAmount(date);
        System.out.println(map);
        return map;
    }
}
