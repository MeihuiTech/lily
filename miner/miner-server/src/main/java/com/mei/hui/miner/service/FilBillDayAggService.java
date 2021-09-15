package com.mei.hui.miner.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.miner.entity.FilBillDayAgg;

/**
* FIL币账单消息每天汇总表
* 
* @description 
* @author shangbin
* @date 2021/8/20 16:45
* @param  
* @return  
* @version v1.4.1
*/
public interface FilBillDayAggService extends IService<FilBillDayAgg> {

    /**
     * 根据minerId、date查询FIL币账单消息每天汇总表
     * @param minerId
     * @param date
     * @return
     */
    public FilBillDayAgg selectFilBillDayAggList(String minerId,String date);

}
