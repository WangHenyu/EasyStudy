package com.why.edu.service.impl;

import com.why.edu.service.EmailCodeService;
import com.why.servicebase.exception.EduException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class EmailCodeServiceImpl implements EmailCodeService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public boolean sendCode(String email) {
        // 判断邮箱格式

        String format="[a-zA-Z0-9_]+@\\w+(\\.com|\\.cn){1}";
        if(!email.matches(format))
            throw new EduException(20001,"邮箱格式不正确");

        // 判断缓存中是否存在
        String code = redisTemplate.opsForValue().get("code:"+email);
        if (!StringUtils.isEmpty(code))
            throw new EduException(20001,"验证码尚未过期，请勿频繁操作");
        // 生成验证码
        code = String.valueOf((int)((Math.random()*9+1)*Math.pow(10,5)));
        // 发送验证码
        boolean success = send(email,code);
        if (!success){
            throw new EduException(20001,"邮件发送失败");
        }
        // 存入到缓存
        System.out.println(code);
        redisTemplate.opsForValue().set("code:"+email,code,5, TimeUnit.MINUTES);
        return true;
    }

    private boolean send(String receiver,String code) {
        String from = "824842053@qq.com(hy在线课程)";
        String subject = "验证码";
        String context = "【EasyStudy】欢迎使用EasyStudy,验证码"+code+",5分钟内有效";
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(receiver);
        mailMessage.setSubject(subject);
        mailMessage.setText(context);
        try{
            javaMailSender.send(mailMessage);
        }catch(Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;
    }
}
