package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.FilBaselinePowerHourAgg;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * filcoin 基线和有效算力聚合表，按天聚合 Mapper 接口
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
public interface FilBaselinePowerHourAggMapper extends BaseMapper<FilBaselinePowerHourAgg> {

    /**
     * 根据类型、开始时间、结束时间查询  filcoin 基线和有效算力聚合表，按小时聚合表 的 全网累计出块份数
     *
     * @param type
     * @param startDate
     * @param endDate
     * @return
     */
    public Long selectFilBaselinePowerHourAggBlocksByType(@Param("type") String type, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
