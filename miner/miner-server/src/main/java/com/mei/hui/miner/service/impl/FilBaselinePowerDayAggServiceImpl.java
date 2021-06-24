package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilBaselinePowerDayAgg;
import com.mei.hui.miner.mapper.FilBaselinePowerDayAggMapper;
import com.mei.hui.miner.service.FilBaselinePowerDayAggService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * filcoin 基线和有效算力聚合表，按天聚合 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Service
public class FilBaselinePowerDayAggServiceImpl extends ServiceImpl<FilBaselinePowerDayAggMapper, FilBaselinePowerDayAgg> implements FilBaselinePowerDayAggService {

}
