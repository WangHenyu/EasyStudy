package com.why.edu.mapper;

import com.why.edu.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-21
 */
public interface UserMapper extends BaseMapper<User> {

    int selectCountByDate(String date);
}
