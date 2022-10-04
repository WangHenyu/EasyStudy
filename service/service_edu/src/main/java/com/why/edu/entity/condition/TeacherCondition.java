package com.why.edu.entity.condition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "教师查询条件对象")
public class TeacherCondition {

    @ApiModelProperty("教师名称")
    private String name;

    @ApiModelProperty("头衔 1高级讲师 2首席讲师")
    private Integer level;

    @ApiModelProperty(value = "查询开始时间",example = "2022-09-09 00:00:00")
    private String begin;

    @ApiModelProperty(value = "查询结束时间",example = "2022-09-09 00:00:00")
    private String end;

}
