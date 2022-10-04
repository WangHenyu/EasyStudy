package com.why.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PublishCourseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private String cover;

    private Integer lessonNum;

    private String subjectLevelOne;

    private String subjectLevelTwo;

    private String teacherName;
    // 只用于显示
    private String price;
}
