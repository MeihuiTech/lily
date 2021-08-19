package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/17 17:09
 **/
@Data
@ApiModel(value = "账单列表入参")
public class FilBillMonthBO extends BasePage {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "矿工id")
    private String minerId;

    @ApiModelProperty(value = "日期：月汇总、日账单接口格式为：2021-08")
    private String monthDate;

    @ApiModelProperty(value = "类型：0Node Fee矿工手续费，1Burn Fee燃烧手续费，2Transfer转账，3BlockAward区块奖励，4其它")
    private Integer type;

    @ApiModelProperty(value = "收支：0支出，1收入")
    private Integer outsideType;

}
