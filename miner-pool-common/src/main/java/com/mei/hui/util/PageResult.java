package com.mei.hui.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel
@AllArgsConstructor
public class PageResult<T> {

    @ApiModelProperty(value = "错误码")
    private String code = ErrorCode.MYB_000000.getCode();

    @ApiModelProperty(value = "错误描述")
    private String msg = ErrorCode.MYB_000000.getMsg();

    @ApiModelProperty(value = "总页数")
    private int total;

    @ApiModelProperty(value = "数据列表")
    private List<T> rows;

    public PageResult(int total,List<T> rows){
        this.total = total;
        this.rows = rows;
    }

}
