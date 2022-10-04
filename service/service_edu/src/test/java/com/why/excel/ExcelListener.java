package com.why.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import java.util.Map;

public class ExcelListener extends AnalysisEventListener<DemoData> {

    @Override // 一行一行地读取excel中的内容
    public void invoke(DemoData data, AnalysisContext analysisContext) {
        System.out.println("用户信息：" + data);
    }

    @Override // 读取excel表头信息
    public void invokeHeadMap(Map headMap, AnalysisContext context) {
        System.out.println("表头信息：" + headMap);
    }

    @Override // 读取完成后执行
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}