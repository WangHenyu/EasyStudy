package com.why.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.LoginLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.User;
import com.why.edu.entity.condition.LogCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-30
 */
public interface LoginLogService extends IService<LoginLog> {

    void saveLoginLog(User user, HttpServletRequest request);

    Map<String, Object> getLoginLogByPage(Page<LoginLog> logPage, LogCondition condition);
}
