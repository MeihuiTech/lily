package com.mei.hui.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

@ApiModel
@Data
public class Result<T> implements Serializable {
    @ApiModelProperty(value = "响应码，000000 表示成功;其他为请求失败")
    private String code;

    @ApiModelProperty(value = "响应描述信息")
    private String msg;

    @ApiModelProperty(value = "响应数据")
    private T data;

    public static Result OK = new Result(ErrorCode.MYB_000000.getCode(),ErrorCode.MYB_000000.getMsg());

    public static Result success(Object data){
        Result result = new Result(ErrorCode.MYB_000000.getCode(),ErrorCode.MYB_000000.getMsg());
        result.setData(data);
        return result;
    }

    public static Result fail(String code,String msg){
        Result result = new Result(code,msg);
        return result;
    }

    /**
     * @param code 错误码
     * @param msg 错误描述信息
     */
    public Result(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public Result(){
    }



}
