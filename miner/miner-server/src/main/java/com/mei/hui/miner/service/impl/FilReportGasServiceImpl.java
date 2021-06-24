package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilReportGas;
import com.mei.hui.miner.feign.vo.ReportGasBO;
import com.mei.hui.miner.mapper.FilReportGasMapper;
import com.mei.hui.miner.service.FilReportGasService;
import com.mei.hui.util.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * gas费用聚合 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Service
public class FilReportGasServiceImpl extends ServiceImpl<FilReportGasMapper, FilReportGas> implements FilReportGasService {

    @Override
    public Result reportGas(ReportGasBO bo) {
        FilReportGas reportGas = new FilReportGas();
        reportGas.setThirtyTwoGas(bo.getThirtyTwoGas())
                .setThirtyTwoCost(bo.getThirtyTwoCost())
                .setThirtyTwoPledge(bo.getThirtyTwoPledge())
                .setSixtyFourGas(bo.getSixtyFourGas())
                .setSixtyFourCost(bo.getSixtyFourCost())
                .setSixtyFourPledge(bo.getSixtyFourPledge())
                .setDate(LocalDateTime.now())
                .setCreateTime(LocalDateTime.now());
        this.save(reportGas);
        return Result.OK;
    }
}
