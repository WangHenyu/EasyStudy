package com.why.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.condition.TeacherCondition;

import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-08
 */
public interface TeacherService extends IService<Teacher> {

    void queryPage(Page<Teacher> page, TeacherCondition condition);

    Map<String, Object> queryTeacherByPage(int current, int limit);

    Map<String, Object> queryTeacherFront(int current, int limit);
}
