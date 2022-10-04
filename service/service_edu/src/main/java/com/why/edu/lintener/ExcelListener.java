package com.why.edu.lintener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.why.edu.entity.Subject;
import com.why.edu.entity.vo.SubjectExcelData;
import com.why.edu.service.SubjectService;
import com.why.servicebase.exception.EduException;

import java.util.Objects;

public class ExcelListener extends AnalysisEventListener<SubjectExcelData>{

    // 因为ExcelListener不交给Spring管理所以无法使用自动注入
    // 需要通过构造方法将subjectService传入进来才能进行数据库操作
    private SubjectService subjectService;
    public ExcelListener(){}
    public ExcelListener(SubjectService subjectService){
        this.subjectService = subjectService;
    }

    @Override // 一行一行地读取excel中的内容
    public void invoke(SubjectExcelData rowData, AnalysisContext analysisContext) {
        if (Objects.isNull(rowData))
            throw new EduException(20001,"文件中没有数据");

        // 判断一级分类是否重复
        Subject oneSubject = existOneSubject(rowData.getLevelOneSubjectName());
        if (Objects.isNull(oneSubject)){
            // 一级分类不重复
            oneSubject = new Subject();
            oneSubject.setParentId("0");
            oneSubject.setTitle(rowData.getLevelOneSubjectName());
            subjectService.save(oneSubject);
        }
        String pId = oneSubject.getId();

        // 判断二级分类是否重复
        Subject twoSubject = existTwoSubject(rowData.getLevelTwoSubjectName(), pId);
        if (Objects.isNull(twoSubject)){
            // 二级分类不重复
            twoSubject = new Subject();
            twoSubject.setParentId(pId);
            twoSubject.setTitle(rowData.getLevelTwoSubjectName());
            subjectService.save(twoSubject);
        }

    }

    // 根据类型名称查询一级分类 若存在则不能添加
    private Subject existOneSubject(String name){
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Subject::getTitle,name);
        wrapper.eq(Subject::getParentId,0);
        return subjectService.getOne(wrapper);
    }

    // 根据类型名称查询二级分类 若存在则不能添加
    private Subject existTwoSubject(String name,String pId){
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Subject::getParentId,pId);
        wrapper.eq(Subject::getTitle,name);
        return subjectService.getOne(wrapper);
    }


    @Override // 读取完成后执行
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
