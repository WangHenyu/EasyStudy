package com.why.edu.entity.condition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "课程查询条件")
public class CourseCondition {

    @ApiModelProperty("课程名称")
    private String title;

    @ApiModelProperty("课程讲师")
    private String teacherName;
}
