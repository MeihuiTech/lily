package com.mei.hui.browser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 近30天有效算力走势
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/19 16:36
 **/
@Data
@TableName("fil_ex_base_line_trend")
public class FilExBaseLineTrend {

    /**
     * 高度
     */
    private Long height;

    /**
     * filecoin时间戳
     */
    private Long timestamp;

    /**
     * 基线算力(byte)
     */
    private BigDecimal baselinePower;

    /**
     * 全网有效算力(byte)
     */
    private BigDecimal totalQaBytesPower;

    private Long createdAt;

    private Long updatedAt;

}
