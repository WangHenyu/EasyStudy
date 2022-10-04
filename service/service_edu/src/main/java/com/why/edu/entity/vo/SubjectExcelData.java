package com.why.edu.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "Excel对应的实体类")
public class SubjectExcelData {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0)
    private String levelOneSubjectName;

    @ExcelProperty(index = 1)
    private String levelTwoSubjectName;
}
