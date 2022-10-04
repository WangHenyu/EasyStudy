package com.why.servicebase.handler;

import com.why.commonutils.Result;
import com.why.servicebase.exception.EduException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 特定异常处理
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Result nullPoint(Exception e){

        e.printStackTrace();
        log.error(e.getMessage());
        return Result.error().message("空指针异常");
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(EduException.class)
    @ResponseBody
    public Result eduException(EduException e){

        e.printStackTrace();
        // 记录日志
        log.error(e.getDetailMessage());
        return Result.error()
                    .code(e.getCode())
                    .message(e.getDetailMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public Result overMaxSize(MaxUploadSizeExceededException e){

        e.printStackTrace();
        return Result.error().message("上传文件大于5MB");
    }

    /**
     * 除了上面声明的异常,剩下的异常都走这个逻辑
     */
    @ExceptionHandler
    @ResponseBody
    public Result error(Exception e){

        e.printStackTrace();
        log.error(e.getMessage());
        return Result.error().message("出现错误了,请联系管理员");
    }



}
