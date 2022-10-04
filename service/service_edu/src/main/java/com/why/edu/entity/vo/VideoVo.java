package com.why.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String courseId;

    private String chapterId;

    private String videoSourceId;

    private String videoOriginalName;

    private String title;

    private Integer sort;

    private Boolean isFree;

}
