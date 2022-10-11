package com.why.edu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.why.commonutils.Result;
import com.why.edu.entity.Banner;
import com.why.edu.service.BannerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 首页banner表 前端控制器
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-19
 */

@RestController
@RequestMapping("/eduservice/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;


    @CacheEvict(value = "banner", allEntries=true)
    @PostMapping("/saveOrUpdate")
    @ApiOperation("更新ID判断添加还是修改轮播图")
    public Result saveOrUpdateBanner(@RequestBody Banner banner){
        if(StringUtils.isEmpty(banner.getId())){
            // 新增轮播图
            boolean save = bannerService.save(banner);
            return save ? Result.ok():Result.error();
        }
        // 修改轮播图
        boolean update = bannerService.updateByBanner(banner);
        return update ? Result.ok():Result.error();
    }



    @CacheEvict(value = "banner", allEntries=true)
    @DeleteMapping("/delete/{bannerId}")
    @ApiOperation("根据ID删除轮播图")
    public Result deleteBanner(@PathVariable("bannerId") String bannerId){
        boolean remove = bannerService.removeByBannerId(bannerId);
        return remove ? Result.ok() : Result.error();
    }


    @GetMapping("/page/{current}/{limit}")
    @ApiOperation("分页查询轮播图")
    public Result queryBannerByPage(@PathVariable("current")int current,
                                    @PathVariable("limit")int limit){

        Page<Banner> page = new Page<>(current, limit);
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Banner::getIsShow).orderByDesc(Banner::getSort);
        // 分页查询所有
        bannerService.page(page, wrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("list",page.getRecords());
        return Result.ok().data(map);
    }


    @GetMapping("/query/four")
    @ApiOperation("查询前台展示的轮播图")
    public Result queryFourBanner(){
        List<Banner> bannerList = bannerService.queryFourBanner();
        return Result.ok().data("list",bannerList);
    }

    @CacheEvict(value = "banner", allEntries=true)
    @GetMapping("/update/show/{bannerId}/{isShow}")
    @ApiOperation("修改轮播图是否前台展示")
    public Result updateIsShow(@PathVariable("bannerId") String bannerId,
                               @PathVariable("isShow") Boolean isShow){
        Banner banner = new Banner();
        banner.setId(bannerId);
        banner.setIsShow(isShow);
        boolean update = bannerService.updateById(banner);
        return update ? Result.ok() : Result.error();
    }
}

