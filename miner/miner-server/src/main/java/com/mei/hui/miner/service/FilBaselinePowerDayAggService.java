package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBaselinePowerDayAgg;
import com.mei.hui.miner.feign.vo.*;
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

    Result<List<GaslineVO>> gasline();

    /**
    * 对外API-全网数据
    *
    * @description
    * @author shangbin
    * @date 2021/7/3 17:40
    * @param []
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.ForeignNetworkVO>
    * @version v1.4.1
    */
    public Result<ForeignNetworkVO> selectForeignNetwork();

    /**
    * 对外API-平台数据
    *
    * @description
    * @author shangbin
    * @date 2021/7/3 17:52
    * @param []
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.ForeignPlatformVO>
    * @version v1.4.1
    */
    public Result<ForeignPlatformVO> selectForeignPlatform();
}
