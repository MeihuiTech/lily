package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.entity.FilBlockAward;
import com.mei.hui.miner.feign.vo.FilBillDayAggArgsVO;
import com.mei.hui.miner.feign.vo.FilBillReportBO;
import com.mei.hui.miner.feign.vo.FilBlockAwardReportBO;
import com.mei.hui.miner.mapper.FilBlockAwardMapper;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.FilBlockAwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/30 19:40
 **/
@Slf4j
@Service
public class FilBlockAwardServiceImpl extends ServiceImpl<FilBlockAwardMapper,FilBlockAward> implements FilBlockAwardService {

    @Autowired
    private FilBlockAwardMapper filBlockAwardMapper;
    @Autowired
    private FilBillService filBillService;

    /*上报fil币区块奖励详情*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportFilBlockAwardMq(FilBlockAwardReportBO filBlockAwardReportBO,FilBillDayAggArgsVO filBillDayAggArgsVO) {
        FilBlockAward filBlockAward = new FilBlockAward();
        BeanUtils.copyProperties(filBlockAwardReportBO,filBlockAward);
        filBlockAward.setMinerId(filBlockAwardReportBO.getMiner());
        filBlockAward.setCid(filBlockAwardReportBO.getCid());
        if (filBlockAwardReportBO.getMinerFee() == null){
            filBlockAward.setMinerFee(BigDecimal.ZERO);
        }
        filBlockAward.setParentWeight(filBlockAwardReportBO.getParentweight());
        filBlockAward.setDateTime(LocalDateTime.ofEpochSecond(filBlockAwardReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8)));
        filBlockAward.setCreateTime(LocalDateTime.now());
        log.info("保存FIL币区块奖励详情入参：【{}】",filBlockAward);
        filBlockAwardMapper.insert(filBlockAward);

        // 在FIL币账单消息详情表里手动插入一条区块奖励数据
        filBillService.insertFilBillBlockAward(filBlockAwardReportBO, filBillDayAggArgsVO);

    }






}
