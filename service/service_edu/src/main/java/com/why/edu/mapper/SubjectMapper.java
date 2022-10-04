package com.why.edu.mapper;

import com.why.edu.entity.Subject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程科目 Mapper 接口
 * </p>
 *
 * @author WangHenYu
 * @since 2022-09-11
 */
@Repository
public interface SubjectMapper extends BaseMapper<Subject> {

    List<Map<String, Object>> selectSubjectData();
}
