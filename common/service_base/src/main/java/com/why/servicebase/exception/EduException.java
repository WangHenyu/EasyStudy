package com.why.servicebase.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EduException extends RuntimeException {

    // 异常状态码
    private Integer code;
    // 异常详细信息
    private String detailMessage;

}
