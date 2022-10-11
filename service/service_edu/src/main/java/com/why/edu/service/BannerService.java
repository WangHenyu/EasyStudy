package com.why.edu.service;

import com.why.edu.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-19
 */
public interface BannerService extends IService<Banner> {

    List<Banner> queryFourBanner();

    boolean removeByBannerId(String bannerId);

    boolean updateByBanner(Banner banner);
}
