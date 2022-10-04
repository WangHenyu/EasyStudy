package com.why.vod.entity.condition;

import lombok.Data;

import java.io.Serializable;

@Data
public class LogCondition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nickname;

    private String courseTitle;

    private String videoTitle;

    private String begin;

    private String end;

}
