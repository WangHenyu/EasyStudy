package com.why.edu.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(description = "课程分类数据返回对象")
public class SubjectVo {

    private static final long serialVersionUID = 1L;

    private String id;

    private String title;

    private List<SubjectVo> children = new ArrayList<>();
}
