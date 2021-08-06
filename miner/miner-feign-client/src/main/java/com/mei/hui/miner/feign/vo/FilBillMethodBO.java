package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/4 17:20
 **/
@Data
@ApiModel(value = "账单管理入参")
public class FilBillMethodBO extends BasePage {

    @ApiModelProperty(value = "矿工id")
    private String minerId;

    @ApiModelProperty(value = "子账户")
    private String subAccount;

    @ApiModelProperty(value = "日期")
    private String monthDate;

    @ApiModelProperty(value = "收支：0支出，1收入")
    private Integer type;

    @ApiModelProperty(value = "方法")
    private String method;

}
