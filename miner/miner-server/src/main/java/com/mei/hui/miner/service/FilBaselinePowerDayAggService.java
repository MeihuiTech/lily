package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBaselinePowerDayAgg;
import com.mei.hui.miner.feign.vo.BaselineAndPowerVO;
import com.mei.hui.miner.feign.vo.GeneralViewVo;
import com.mei.hui.util.Result;

import java.util.List;

/**
 * <p>
 * filcoin 基线和有效算力聚合表，按天聚合 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
public interface FilBaselinePowerDayAggService extends IService<FilBaselinePowerDayAgg> {

    Result<GeneralViewVo> generalView();

    Result<List<BaselineAndPowerVO>> baselineAndPower();

}
