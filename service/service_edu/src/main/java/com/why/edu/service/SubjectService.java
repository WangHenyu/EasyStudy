package com.why.edu.service;

import com.why.edu.entity.Subject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.why.edu.entity.vo.SubjectVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-11
 */
public interface SubjectService extends IService<Subject> {

    boolean saveByExcel(SubjectService subjectService, MultipartFile file);

    List<SubjectVo> queryAllSubject();

    boolean deleteById(String id);

    List<Map<String, Object>> getSubjectData();
}
