package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilBillDayAgg;
import com.mei.hui.miner.mapper.FilBillDayAggMapper;
import com.mei.hui.miner.service.FilBillDayAggService;
import com.mei.hui.util.DateUtils;
import jdk.nashorn.internal.runtime.linker.LinkerCallSite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* FIL币账单消息每天汇总表
*
* @description
* @author shangbin
* @date 2021/8/20 16:46
* @param
* @return
* @version v1.4.1
*/
@Service
@Slf4j
public class FilBillDayAggServiceImpl extends ServiceImpl<FilBillDayAggMapper, FilBillDayAgg> implements FilBillDayAggService {

    @Autowired
    private FilBillDayAggMapper filBillDayAggMapper;

    /*根据minerId、date查询FIL币账单消息每天汇总表*/
    @Override
    public FilBillDayAgg selectFilBillDayAggList(String minerId,String date){
        QueryWrapper queryWrapper = new QueryWrapper();
        FilBillDayAgg filBillDayAgg = new FilBillDayAgg();
        filBillDayAgg.setMinerId(minerId);
        filBillDayAgg.setDate(DateUtils.lDTStringToLocalDateYMD(date));
        queryWrapper.setEntity(filBillDayAgg);
        FilBillDayAgg dbFilBillDayAgg = filBillDayAggMapper.selectOne(queryWrapper);
        return dbFilBillDayAgg;
    }

}
