package com.why.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData{
    @ExcelProperty(/*value = "用户名",*/index = 0)
    private String username;
    @ExcelProperty(/*value = "用户密码",*/index = 1)
    private String password;
}