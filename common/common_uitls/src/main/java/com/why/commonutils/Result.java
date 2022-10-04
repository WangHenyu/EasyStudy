package com.why.commonutils;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Result {

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("状态码")
    private Integer code;

    @ApiModelProperty("返回消息")
    private String message;

    @ApiModelProperty("返回数据")
    private Map<String,Object> data = new HashMap<>();

    /**
     * 构造函数私有化的目的是
     * 不想让你手动去new Result而是使用我提供好的Result
     */
    private Result(){}

    /**
     * 成功的返回结果
     */
    public static Result ok(){
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        return result;
    }
    public static Result ok(String key,Object value){
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        result.getData().put(key,value);
        return result;
    }
    /**
     * 失败的返回结果
     */
    public static Result error(){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR);
        result.setMessage("失败");
        return result;
    }
    public static Result error(String key,Object value){
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR);
        result.setMessage("失败");
        result.getData().put(key,value);
        return result;
    }

    /**
     * 单独设置某个属性
     * return this 使用链式编程
     */
    public Result success(Boolean success){
        this.setSuccess(success);
        return this;
    }
    public Result code(Integer code){
        this.setCode(code);
        return this;
    }
    public Result message(String message){
        this.setMessage(message);
        return this;
    }
    public Result data(String key,Object value){
        this.data.put(key,value);
        return this;
    }
    public Result data(Map<String,Object> data){
        this.setData(data);
        return this;
    }
}
