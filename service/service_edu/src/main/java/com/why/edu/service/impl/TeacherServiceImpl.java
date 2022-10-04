package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.Teacher;
import com.why.edu.entity.condition.TeacherCondition;
import com.why.edu.mapper.TeacherMapper;
import com.why.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-08
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Override
    public void queryPage(Page<Teacher> page, TeacherCondition condition) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();

        if (Objects.isNull(condition)){
            baseMapper.selectPage(page,wrapper);
            return;
        }

        String name = condition.getName();
        Integer level = condition.getLevel();
        String begin = condition.getBegin();
        String end = condition.getEnd();

        if (!StringUtils.isEmpty(name))
            wrapper.like(Teacher::getName,name);
        if (!Objects.isNull(level))
            wrapper.eq(Teacher::getLevel,level);
        if ((!StringUtils.isEmpty(begin)))
            wrapper.ge(Teacher::getGmtCreate,begin);
        if ((!StringUtils.isEmpty(end)))
            wrapper.le(Teacher::getGmtCreate,end);

        wrapper.orderByDesc(Teacher::getGmtCreate);
        baseMapper.selectPage(page,wrapper);
        return;

    }

    @Override
    public Map<String, Object> queryTeacherByPage(int current, int limit) {
        Page<Teacher> teacherPage = new Page<>(current,limit);
        baseMapper.selectPage(teacherPage,null);

        Map<String,Object> map = new HashMap<>();
        map.put("total",teacherPage.getTotal());
        map.put("teachers",teacherPage.getRecords());
        return map;
    }

    @Override
    public Map<String, Object> queryTeacherFront(int current, int limit) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Teacher::getLevel);
        Page<Teacher> teacherPage = new Page<>(current,limit);
        baseMapper.selectPage(teacherPage,wrapper);

        Map<String,Object> map = new HashMap<>();
        map.put("size",teacherPage.getSize());
        map.put("pages",teacherPage.getPages());
        map.put("total",teacherPage.getTotal());
        map.put("hasNext",teacherPage.hasNext());
        map.put("current",teacherPage.getCurrent());
        map.put("teacherList",teacherPage.getRecords());
        map.put("hasPrevious",teacherPage.hasPrevious());
        return map;
    }
}
