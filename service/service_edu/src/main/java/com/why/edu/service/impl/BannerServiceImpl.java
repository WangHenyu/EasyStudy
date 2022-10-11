package com.why.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.why.edu.entity.Banner;
import com.why.edu.mapper.BannerMapper;
import com.why.edu.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.why.commonconst.RabbitConst.EXCHANGE_DIRECT_OSS;
import static com.why.commonconst.RabbitConst.ROUTING_KEY_OSS_DELETE;

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

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    @Cacheable(value = "banner",key = "'indexBannerList'")
    public List<Banner> queryFourBanner() {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getIsShow,true);
        wrapper.orderByDesc(Banner::getSort);
        return baseMapper.selectList(wrapper);

    }

    @Override
    public boolean removeByBannerId(String bannerId) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Banner::getImageUrl);
        wrapper.eq(Banner::getId,bannerId);
        Banner banner = baseMapper.selectOne(wrapper);
        if (!StringUtils.isEmpty(banner.getImageUrl())){
            // 删除banner图片
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_OSS,ROUTING_KEY_OSS_DELETE,banner.getImageUrl());
        }
        // 删除banner
        int delete = baseMapper.deleteById(bannerId);
        return delete > 0;
    }

    @Override
    public boolean updateByBanner(Banner banner) {
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Banner::getImageUrl);
        wrapper.eq(Banner::getId,banner.getId());
        Banner oldBanner = baseMapper.selectOne(wrapper);
        String imageUrl = oldBanner.getImageUrl();
        if (!StringUtils.isEmpty(imageUrl) && !imageUrl.equals(banner.getImageUrl())){
            // 删除旧的banner图片
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT_OSS,ROUTING_KEY_OSS_DELETE,imageUrl);
        }
        int update = baseMapper.updateById(banner);
        return update > 0;
    }
}
