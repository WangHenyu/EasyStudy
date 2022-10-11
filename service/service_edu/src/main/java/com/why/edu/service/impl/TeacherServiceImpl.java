package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.Teacher;
import com.why.edu.entity.condition.TeacherCondition;
import com.why.edu.mapper.TeacherMapper;
import com.why.edu.service.TeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.why.commonconst.RabbitConst.*;

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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void queryPage(Page<Teacher> page, TeacherCondition condition) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        if (Objects.isNull(condition)){
            baseMapper.selectPage(page,wrapper);
            return;
        }
        Integer level = condition.getLevel();
        String end = condition.getEnd();
        String name = condition.getName();
        String begin = condition.getBegin();
        wrapper.like(!StringUtils.isEmpty(name),Teacher::getName,name);
        wrapper.eq(!Objects.isNull(level),Teacher::getLevel,level);
        wrapper.ge(!StringUtils.isEmpty(begin),Teacher::getGmtCreate,begin);
        wrapper.le(!StringUtils.isEmpty(end),Teacher::getGmtCreate,end);
        wrapper.orderByDesc(Teacher::getGmtCreate);
        baseMapper.selectPage(page,wrapper);
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

    @Override
    public boolean removeByTeacherId(String teacherId) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Teacher::getAvatar);
        wrapper.eq(Teacher::getId,teacherId);
        Teacher teacher = baseMapper.selectOne(wrapper);
        String avatarUrl = teacher.getAvatar();
        // 发送删除头像消息给rabbitMq
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_OSS,ROUTING_KEY_OSS_DELETE,avatarUrl);
        // 删除讲师信息
        int delete = baseMapper.deleteById(teacherId);
        return delete > 0;
    }

    @Override
    public boolean updateByTeacher(Teacher teacher) {
        LambdaQueryWrapper<Teacher> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Teacher::getAvatar);
        wrapper.eq(Teacher::getId,teacher.getId());
        Teacher oldTeacher = baseMapper.selectOne(wrapper);
        String oldAvatarUrl = oldTeacher.getAvatar();
        if (!StringUtils.isEmpty(oldAvatarUrl) && !oldAvatarUrl.equals(teacher.getAvatar())){
            // 删除旧头像
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_OSS,ROUTING_KEY_OSS_DELETE,oldAvatarUrl);
        }
        int update = baseMapper.updateById(teacher);
        return update > 0;
    }
}


