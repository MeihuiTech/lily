package com.mei.hui.miner.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class GetTransferRecordByIdBO {

    @ApiModelProperty(value = "系统划转记录表主键ID",required = true)
    private Long id;
}
