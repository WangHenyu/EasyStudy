package com.why.edu.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.why.edu.entity.Subject;
import com.why.edu.entity.vo.SubjectExcelData;
import com.why.edu.entity.vo.SubjectVo;
import com.why.edu.lintener.ExcelListener;
import com.why.edu.mapper.SubjectMapper;
import com.why.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.why.servicebase.exception.EduException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-11
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override // 通过Excel导入课程分类
    public boolean saveByExcel(SubjectService subjectService, MultipartFile file) {

        if (Objects.isNull(file))
            throw new EduException(20001,"不能上传空文件");

        // 文件后缀判断
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))
            throw new EduException(20001,"请上传后缀为.xlsx或.xls的Excel文件");

        try{
            InputStream inputStream = file.getInputStream();
            // 读取excel
            EasyExcel.read(inputStream, SubjectExcelData.class,new ExcelListener(subjectService)).sheet().doRead();
        }catch(Exception exception){
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    @Override // 查询所有分类
    public List<SubjectVo> queryAllSubject() {
        // 查询所有1级分类
        LambdaQueryWrapper<Subject> levelOneWrapper = new LambdaQueryWrapper<>();
        levelOneWrapper.eq(Subject::getParentId,"0");
        List<Subject> levelOneSub = baseMapper.selectList(levelOneWrapper);
        // 查询所有2级分类
        LambdaQueryWrapper<Subject> levelTwoWrapper = new LambdaQueryWrapper<>();
        levelOneWrapper.ne(Subject::getParentId,"0");
        List<Subject> levelTwoSub = baseMapper.selectList(levelTwoWrapper);

        List<SubjectVo> allSubjectVo = new ArrayList<>(); // 所有分类
        // 封装1级分类
        levelOneSub.stream().forEach(subjectOne -> {
            SubjectVo levelOneOv = new SubjectVo();
            levelOneOv.setId(subjectOne.getId());
            levelOneOv.setTitle(subjectOne.getTitle());
            // 存放子分类的集合
            List<SubjectVo> childrenSubject = new ArrayList<>();
            // 封装二级分类
            levelTwoSub.stream()
                        .filter(subjectTwo->subjectTwo.getParentId().equals(subjectOne.getId()))
                        .forEach(subjectTwo->{
                            SubjectVo levelTwoOv = new SubjectVo();
                            BeanUtils.copyProperties(subjectTwo,levelTwoOv);
                            childrenSubject.add(levelTwoOv);
                        });
            // 添加二级分类
            levelOneOv.setChildren(childrenSubject);
            allSubjectVo.add(levelOneOv);
        });
        return allSubjectVo;
    }

    @Override //根据Id删除
    public boolean deleteById(String id) {
        Subject subject = baseMapper.selectById(id);
        if (Objects.isNull(subject))
            throw new EduException(20001,"分类不存在");

        if ("0".equals(subject.getParentId())){
            // 删除一级分类和他的子分类
            // 获取子分类
            List<Subject> childrens = baseMapper.selectList(new LambdaQueryWrapper<Subject>().eq(Subject::getParentId, id));
            if (childrens.size() > 0){
                List<String> childrenIds = childrens.stream().map(Subject::getId)
                                                    .collect(Collectors.toList());
                // 批量删除子分类
                baseMapper.deleteBatchIds(childrenIds);
            }
        }
        // 删除当前分类
        int delete = baseMapper.deleteById(id);
        return delete > 0;
    }

    @Override
    public List<Map<String, Object>> getSubjectData() {
        List<Map<String, Object>> data = baseMapper.selectSubjectData();
        return data;
    }
}
