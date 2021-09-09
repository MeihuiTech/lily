package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.FilBillDayAgg;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/19 16:40
 **/
@Repository
public interface FilBillDayAggMapper extends BaseMapper<FilBillDayAgg> {

    /**
     * 根据矿工id、日期更新所有的收入和支出
     *
     * @param minerId
     * @param date
     * @param inMoney
     * @param outMoney
     * @param balance
     * @param inTransfer
     * @param outTransfer
     * @param outNodeFee
     * @param outBurnFee
     * @param outOther
     * @return
     */
    public Integer updateFilBillDayAggByMinerIdAndDate(@Param("minerId") String minerId, @Param("date") String date,
                                                       @Param("inMoney") BigDecimal inMoney, @Param("outMoney") BigDecimal outMoney, @Param("balance") BigDecimal balance,
                                                       @Param("inTransfer") BigDecimal inTransfer,@Param("inBlockAward") BigDecimal inBlockAward,
                                                       @Param("outTransfer") BigDecimal outTransfer, @Param("outNodeFee") BigDecimal outNodeFee,
                                                       @Param("outBurnFee") BigDecimal outBurnFee, @Param("outOther") BigDecimal outOther);
}
