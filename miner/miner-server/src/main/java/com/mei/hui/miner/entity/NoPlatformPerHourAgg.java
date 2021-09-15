package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 非平台矿工,每小时出块数
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class NoPlatformPerHourAgg implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String minerId;

    /**
     * 累计出块份数
     */
    private Long totalBlocks;

    /**
     * 每小时新增出块数
     */
    private Long perHourBlocks;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
