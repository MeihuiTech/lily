package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.PageResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class BillAggVO {

    @ApiModelProperty("分页信息")
    private PageResult<FilBillPageListVO> page;

    @ApiModelProperty("收入信息")
    private BillInVO in;

    @ApiModelProperty("收入信息")
    private BillOutVO out;


}
