package com.mei.hui.browser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:43
 **/
@Data
@TableName("block_headers")
public class BlockHeaders {


    private String cid;

    private String parentWeight;

    private String parentStateRoot;

    private Integer height;

    private String miner;

    private Integer timestamp;

    private Integer winCount;

    private String parentBaseFee;

    private Integer forkSignaling;

}
