package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.edu.entity.CourseCollect;
import com.why.edu.entity.vo.front.CollectionVo;
import com.why.edu.mapper.CourseCollectMapper;
import com.why.edu.service.CourseCollectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程收藏 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-10-03
 */
@Service
public class CourseCollectServiceImpl extends ServiceImpl<CourseCollectMapper, CourseCollect> implements CourseCollectService {


    @Override
    public Map<String, Object> getCollectionPageByUserId(int current, int limit, String userId) {
        if (limit> 20) limit = 20;
        if (current<1) current = 1;
        current = (current-1)* limit;
        List<CollectionVo> collectionList = baseMapper.selectCollectListByUserId(current,limit,userId);
        LambdaQueryWrapper<CourseCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseCollect::getUserId,userId);
        Integer total = baseMapper.selectCount(wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("list",collectionList);
        return map;
    }
}
