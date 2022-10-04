package com.why.edu.entity.vo;

import com.why.edu.entity.Course;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "编辑课程基本信息的表单对象")
public class CourseVo extends Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "课程简介")
    private String description;

    @ApiModelProperty(value = "教师名称")
    private String teacherName;
}
