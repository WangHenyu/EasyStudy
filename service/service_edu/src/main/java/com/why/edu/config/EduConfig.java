package com.why.edu.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@MapperScan(basePackages = "com.why.edu.mapper")
public class EduConfig {

    /**
     * SQL执行性能分析插件
     * Profile 设置在哪个环境下启用
     */
    @Bean
    @Profile({"dev"})
    public PerformanceInterceptor performanceInterceptor() {

        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        // 超过此处设置的ms则sql不执行
        performanceInterceptor.setMaxTime(200);
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }

    @Bean
    public ISqlInjector SqlInjector(){
        // mybatis-plus逻辑删除插件
        return new LogicSqlInjector();
    }

    @Bean
    public PaginationInterceptor paginationInterceptor(){
        // mybatis-plus分页插件
        return new PaginationInterceptor();
    }

}
