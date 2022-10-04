package com.why.servicebase.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientCourseVo {

    private String id;

    private String title;

    private String cover;

    private BigDecimal price;

    private String teacherName;

    private String videoTitle;
}
