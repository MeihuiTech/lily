package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilReportNetworkData;
import com.mei.hui.miner.feign.vo.ReportNetworkDataBO;
import com.mei.hui.util.Result;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 全网数据上报记录表 服务类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
public interface FilReportNetworkDataService extends IService<FilReportNetworkData> {

    Result reportNetworkData(ReportNetworkDataBO bo);

}
