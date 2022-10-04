package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.LoginLog;
import com.why.edu.entity.User;
import com.why.edu.entity.condition.LogCondition;
import com.why.edu.mapper.UserLoginLogMapper;
import com.why.edu.service.LoginLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.edu.utils.IpUtils;
import com.why.servicebase.exception.EduException;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-30
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<UserLoginLogMapper, LoginLog> implements LoginLogService {


    @Override
    public void saveLoginLog(User user, HttpServletRequest request) {
        try {
            String userAgent = request.getHeader("User-Agent");
            UserAgent ua = UserAgent.parseUserAgentString(userAgent);
            // 获取操作系统对象
            OperatingSystem os = ua.getOperatingSystem();
            // 获取浏览器对象
            Browser browser = ua.getBrowser();
            String osName = os.getName();
            String browserName = browser.getName();
            String equipment = osName + "|" + browserName;
            // 获取IP地址
            String ipAddr = IpUtils.getIpAddr(request);
            // 获取IP所在地
            String cityInfo = IpUtils.getCityInfo(ipAddr);
            LoginLog loginLog = new LoginLog();
            loginLog.setLoginIp(ipAddr);
            loginLog.setLoginAddr(cityInfo);
            loginLog.setLoginEquipment(equipment);
            loginLog.setUserId(user.getId());
            loginLog.setNickname(user.getNickname());
            baseMapper.insert(loginLog);
        } catch (Exception e) {
            e.printStackTrace();
            throw new EduException(20001,"获取地址信息失败");
        }
    }

    @Override
    public Map<String, Object> getLoginLogByPage(Page<LoginLog> logPage, LogCondition condition) {
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(LoginLog::getId,
                       LoginLog::getNickname,
                       LoginLog::getLoginIp,
                       LoginLog::getLoginAddr,
                       LoginLog::getLoginEquipment,
                       LoginLog::getGmtCreate);
        if (!StringUtils.isEmpty(condition.getEnd())){
            condition.setEnd(condition.getEnd() + " 23:59:59");
        }
        wrapper.like(!StringUtils.isEmpty(condition.getNickname()),LoginLog::getNickname,condition.getNickname());
        wrapper.like(!StringUtils.isEmpty(condition.getLoginAddr()),LoginLog::getLoginAddr,condition.getLoginAddr());
        wrapper.ge(!StringUtils.isEmpty(condition.getBegin()),LoginLog::getGmtCreate,condition.getBegin());
        wrapper.le(!StringUtils.isEmpty(condition.getEnd()),LoginLog::getGmtCreate,condition.getEnd());
        wrapper.orderByDesc(LoginLog::getGmtCreate);
        baseMapper.selectPage(logPage,wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("total",logPage.getTotal());
        map.put("list",logPage.getRecords());
        return map;
    }
}
