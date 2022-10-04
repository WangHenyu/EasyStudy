package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.why.edu.entity.Banner;
import com.why.edu.mapper.BannerMapper;
import com.why.edu.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-19
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {


    @Override
    @Cacheable(value = "banner",key = "'indexBannerList'")
    public List<Banner> queryFourBanner() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getIsShow,true);
        wrapper.orderByDesc(Banner::getSort);
        return baseMapper.selectList(wrapper);

    }
}
