package com.why.excel;

import com.alibaba.excel.EasyExcel;

import java.util.ArrayList;
import java.util.List;

public class ExcelDemo {

    public static void main(String[] args) {

        // EasyExcel写入操作示例
        // String fileName = "C:\\Users\\uuu\\Desktop\\demo.xlsx";
        // EasyExcel.write(fileName,DemoData.class).sheet("表1").doWrite(data());

        // EasyExcel读取文件操作示例
        String fileName = "C:\\Users\\uuu\\Desktop\\demo.xlsx";
        EasyExcel.read(fileName,DemoData.class,new ExcelListener()).sheet("表1").doRead();

    }

    public static List<DemoData> data(){
        List<DemoData> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setUsername("admin"+i);
            data.setPassword("12345"+i);
            dataList.add(data);
        }

        return dataList;
    }
}


