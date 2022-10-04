package com.why.edu.entity.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description="用于前台页面展示")
public class UserInfoVo {

    private String id;

    private String nickname;

    private String avatar;
}
