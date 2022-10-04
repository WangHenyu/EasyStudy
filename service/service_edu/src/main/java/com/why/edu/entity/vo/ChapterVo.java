package com.why.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ChapterVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String title;

    private String courseId;

    private Integer sort;

    private List<VideoVo> videos = new ArrayList<>();
}
