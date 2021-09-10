package com.mei.hui.miner.feign.vo;

import com.mei.hui.util.BasePage;
import com.mei.hui.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/17 17:09
 **/
@ApiModel(value = "账单列表入参")
public class FilBillMonthBO extends BasePage {

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "矿工id")
    private String minerId;

    @ApiModelProperty(value = "开始日期：月汇总、日账单接口格式为：2021-08")
    private LocalDateTime startMonthDate;

    @ApiModelProperty(value = "结束日期：月汇总、日账单接口格式为：2021-08")
    private LocalDateTime endMonthDate;

    @ApiModelProperty(value = "类型：0Node Fee矿工手续费，1Burn Fee燃烧手续费，2Transfer转账，3BlockAward区块奖励，4Other其它")
    private Integer type;

    @ApiModelProperty(value = "收支：0支出，1收入")
    private Integer outsideType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMinerId() {
        return minerId;
    }

    public void setMinerId(String minerId) {
        this.minerId = minerId;
    }

    public LocalDateTime getStartMonthDate() {
        return startMonthDate;
    }

    public void setStartMonthDate(String startMonthDate) {
        if(StringUtils.isNotEmpty(startMonthDate)){
            String[] startArray = startMonthDate.split("-");
            LocalDateTime startDate = LocalDateTime.now().withYear(Integer.valueOf(startArray[0]))
                    .withMonth(Integer.valueOf(startArray[1])).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            this.startMonthDate = startDate;
        }
    }

    public LocalDateTime getEndMonthDate() {
        return endMonthDate;
    }

    public void setEndMonthDate(String endMonthDate) {
        if(StringUtils.isNotEmpty(endMonthDate)){
            String[] endArray = endMonthDate.split("-");
            int day = DateUtils.getDays(Integer.valueOf(endArray[0]), Integer.valueOf(endArray[1]));
            LocalDateTime endDate = LocalDateTime.now().withYear(Integer.valueOf(endArray[0]))
                    .withMonth(Integer.valueOf(endArray[1])).withDayOfMonth(day).withHour(23).withMinute(59).withSecond(59).withNano(999);
            this.endMonthDate = endDate;
        }
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOutsideType() {
        return outsideType;
    }

    public void setOutsideType(Integer outsideType) {
        this.outsideType = outsideType;
    }


}
