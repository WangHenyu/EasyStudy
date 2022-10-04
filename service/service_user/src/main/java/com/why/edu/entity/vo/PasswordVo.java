package com.why.edu.entity.vo;

import lombok.Data;

@Data
public class PasswordVo {

    private String oldPwd;

    private String newPwd;

    private String confirm;
}
