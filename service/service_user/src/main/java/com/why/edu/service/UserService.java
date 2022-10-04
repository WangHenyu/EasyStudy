package com.why.edu.service;

import com.why.edu.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.vo.LoginVo;
import com.why.edu.entity.vo.PasswordVo;
import com.why.edu.entity.vo.RegisterVo;
import com.why.edu.entity.vo.UserInfoVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-21
 */
public interface UserService extends IService<User> {

    String login(LoginVo loginVo,HttpServletRequest request);

    boolean register(RegisterVo registerVo);

    UserInfoVo queryUserInfo(String userId);

    int getCountByDate(String date);

    boolean updateUser(User user);

    boolean updatePassword(PasswordVo passwordVo, HttpServletRequest request);
}
