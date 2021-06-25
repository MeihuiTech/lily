package com.mei.hui.miner.model;

import com.mei.hui.miner.entity.FilDeadlines;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/25 16:20
 **/
@Data
@ApiModel("用户首页WindowPoSt的96个窗口出参")
public class FilDeadlinesNinetySixVO {

    @ApiModelProperty(value = "今天矿工窗口记录")
    private List<FilDeadlinesListVO> todayFilDeadlinesList;

    @ApiModelProperty(value = "昨天矿工窗口记录")
    private List<FilDeadlinesListVO> yesterdayFilDeadlinesList;

    @ApiModelProperty(value = "当前窗口序号")
    private Integer deadline;


}
