package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBaselinePowerHourAgg;
import com.mei.hui.miner.feign.vo.ForeignNetworkVO;
import com.mei.hui.miner.feign.vo.ForeignPlatformVO;
import com.mei.hui.miner.feign.vo.GeneralViewVo;
import com.mei.hui.util.Result;

/**
 * <p>
 * filcoin 基线和有效算力聚合表，按天聚合 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
public interface FilBaselinePowerHourAggService extends IService<FilBaselinePowerHourAgg> {

    Result<GeneralViewVo> generalView();

}
