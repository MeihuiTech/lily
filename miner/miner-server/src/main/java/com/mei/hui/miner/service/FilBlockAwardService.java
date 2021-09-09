package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBlockAward;
import com.mei.hui.miner.feign.vo.FilBillDayAggArgsVO;
import com.mei.hui.miner.feign.vo.FilBlockAwardReportBO;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/30 19:39
 **/
public interface FilBlockAwardService extends IService<FilBlockAward> {

    /**
    * 上报fil币区块奖励详情
    *
    * @description
    * @author shangbin
    * @date 2021/8/18 10:54
    * @param [filBlockAwardReportBO]
    * @return void
    * @version v1.4.1
    */
    public void reportFilBlockAwardMq(FilBlockAwardReportBO filBlockAwardReportBO, FilBillDayAggArgsVO filBillDayAggArgsVO);
}
