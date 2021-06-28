package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilReportNetworkData;
import com.mei.hui.miner.feign.vo.ReportNetworkDataBO;
import com.mei.hui.miner.mapper.FilReportNetworkDataMapper;
import com.mei.hui.miner.service.FilReportNetworkDataService;
import com.mei.hui.util.Result;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 全网数据上报记录表 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Service
public class FilReportNetworkDataServiceImpl extends ServiceImpl<FilReportNetworkDataMapper, FilReportNetworkData> implements FilReportNetworkDataService {

    @Override
    public Result reportNetworkData(ReportNetworkDataBO bo) {
        FilReportNetworkData reportNetworkData = new FilReportNetworkData();
        reportNetworkData.setActiveMiner(bo.getActiveMiner())
                .setBlockHeight(bo.getBlockHeight())
                .setBlocks(bo.getBlocks())
                .setPower(bo.getPower())
                .setTotalBlockAward(bo.getTotalBlockAward())
                .setUpdateTime(LocalDateTime.now());
        /**
         * 检查全网数据是否存在
         */
        List<FilReportNetworkData> list = this.list();
        if(list.size() >0){
            FilReportNetworkData data = list.get(0);
            reportNetworkData.setId(data.getId());
            this.updateById(reportNetworkData);
        }else{
            this.save(reportNetworkData);
        }
        return Result.OK;
    }
}
