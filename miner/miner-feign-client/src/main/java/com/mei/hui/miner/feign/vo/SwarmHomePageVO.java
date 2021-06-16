package com.mei.hui.miner.feign.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApiModel
@Data
public class SwarmHomePageVO {

    @ApiModelProperty(value = "总出票资产",required = true)
    private BigDecimal totalMoney;

    @ApiModelProperty(value = "累计有效出票数",required = true)
    private long totalTicketValid;

    @ApiModelProperty(value = "累计无效出票数",required = true)
    private long totalTicketAvail;

    @ApiModelProperty(value = "昨天有效出票数",required = true)
    private long yesterdayTicketValid;

    @ApiModelProperty(value = "昨天无效出票数",required = true)
    private long yesterdayTicketAvail;

    @ApiModelProperty(value = "在线节点数",required = true)
    private long onlineNodeNum;

    @ApiModelProperty(value = "离线节点数",required = true)
    private long offlineNodeNum;

    @ApiModelProperty(value = "总的连接数",required = true)
    private long totalLinkNum;

    @ApiModelProperty(value = "连接数图表数据",required = true)
    private List<LinkVO> links = new ArrayList<>();

    @ApiModelProperty(value = "有效票数图表数据",required = true)
    private List<TicketValidVO> ticketValids = new ArrayList<>();

    @ApiModelProperty(value = "可兑换BZZ图表数据",required = true)
    private List<ConvertBzzVO> bzz = new ArrayList<>();
}
