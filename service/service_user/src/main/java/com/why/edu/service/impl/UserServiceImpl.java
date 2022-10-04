package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.commonutils.JwtUtils;
import com.why.commonutils.MD5Utils;
import com.why.edu.entity.User;
import com.why.edu.entity.vo.LoginVo;
import com.why.edu.entity.vo.PasswordVo;
import com.why.edu.entity.vo.RegisterVo;
import com.why.edu.entity.vo.UserInfoVo;
import com.why.edu.mapper.UserMapper;
import com.why.edu.service.LoginLogService;
import com.why.edu.service.UserService;
import com.why.servicebase.exception.EduException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-21
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private LoginLogService loginLogService;


    @Override
    public String login(LoginVo loginVo,HttpServletRequest request) {

        // 前台用户提交的邮箱
        String email = loginVo.getEmail();
        // 前台用户提交的密码
        String password = loginVo.getPassword();

        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password))
            throw new EduException(20001,"请填写邮箱或密码");

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,email);
        Integer count = baseMapper.selectCount(wrapper);
        if (count == null || count == 0)
            throw new EduException(20001,"用户尚未注册");

        User user = baseMapper.selectOne(wrapper);
        String dbPassword = user.getPassword();
        // 获取加密后的密码
        String encryptPassword = MD5Utils.encrypt(password);
        if (!encryptPassword.equals(dbPassword))
            throw new EduException(20001,"密码错误");
        if (user.getIsDisabled())
            throw new EduException(20001,"用户已被禁用");
        loginLogService.saveLoginLog(user,request);
        String token = JwtUtils.getJwtToken(user.getId(),user.getNickname());
        return token;
    }

    @Override
    public boolean register(RegisterVo registerVo) {

        String code = registerVo.getCode();
        String email = registerVo.getEmail();
        String password = registerVo.getPassword();
        String nickname = registerVo.getNickname();

        if(StringUtils.isEmpty(code)||StringUtils.isEmpty(email)||
           StringUtils.isEmpty(password)||StringUtils.isEmpty(nickname)){
            throw new EduException(20001,"请输入昵称/邮箱/密码/验证码");
        }

        String redisCode = redisTemplate.opsForValue().get("code:"+email);
        if (!code.equals(redisCode))
            throw new EduException(20001,"验证码错误");

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail,email);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new EduException(20001, "此邮箱已经被注册");
        }

        // 将注册信息存入数据库
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setIsDisabled(false);
        user.setPassword(MD5Utils.encrypt(password));
        user.setAvatar("");
        int insert = baseMapper.insert(user);
        return insert == 1;
    }

    @Override
    public UserInfoVo queryUserInfo(String userId) {
        UserInfoVo userInfoVo = new UserInfoVo();
        User user = baseMapper.selectById(userId);
        BeanUtils.copyProperties(user,userInfoVo);
        return userInfoVo;
    }

    @Override
    public int getCountByDate(String date) {
        int count = baseMapper.selectCountByDate(date);
        return count;
    }

    @Override
    public boolean updateUser(User user) {
        Date threeDaysAgo = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 3);
        if (user.getGmtModified().compareTo(threeDaysAgo) != -1){
            throw new EduException(20001,"您已在三天内修改过一次,请勿重复修改");
        }
        if (!StringUtils.isEmpty(user.getMobile())){
            String format="^1[3-9]\\d{9}$";
            if(!user.getMobile().matches(format))
                throw new EduException(20001,"手机号码格式不正确");
        }
        baseMapper.updateById(user);
        return true;
    }

    @Override
    public boolean updatePassword(PasswordVo passwordVo,HttpServletRequest request) {
        if (StringUtils.isEmpty(passwordVo.getOldPwd())||
            StringUtils.isEmpty(passwordVo.getNewPwd())||
            StringUtils.isEmpty(passwordVo.getConfirm())){
            return false;
        }
        if (!passwordVo.getNewPwd().equals(passwordVo.getConfirm()))
            throw new EduException(20001,"两次输入的密码不一致，请重新输入");
        String userId = JwtUtils.getUserIdWithJwtToken(request);
        User user = baseMapper.selectById(userId);
        if (StringUtils.isEmpty(user.getPassword()) || StringUtils.isEmpty(user.getEmail()))
            throw new EduException(20001,"暂未绑定邮箱");
        // 加密后比较
        String oldPassword = MD5Utils.encrypt(passwordVo.getOldPwd());
        String newPassword = MD5Utils.encrypt(passwordVo.getNewPwd());
        if (!oldPassword.equals(user.getPassword()))
            throw new EduException(20001,"旧密码错误");
        // 加密后存入数据库
        user.setPassword(newPassword);
        baseMapper.updateById(user);
        return true;
    }
}
