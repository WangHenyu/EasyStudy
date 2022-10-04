package com.why.edu.config;

import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.why.edu.mapper")
public class MybatisPlusConfig {

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
