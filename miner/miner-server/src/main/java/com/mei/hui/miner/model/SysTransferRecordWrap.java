package com.mei.hui.miner.model;

import com.mei.hui.miner.entity.SysTransferRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class SysTransferRecordWrap extends SysTransferRecord
{
    /** 验证码 */
    private String verifyCode;

    @ApiModelProperty(value = "矿工id",required = true)
    private String minerId;


}
