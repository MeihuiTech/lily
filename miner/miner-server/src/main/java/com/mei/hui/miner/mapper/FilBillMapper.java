package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.mei.hui.miner.entity.FilBill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * FIL币账单 Mapper 接口
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-21
 */
@Repository
public interface FilBillMapper extends BaseMapper<FilBill> {

    IPage<FilBill> getBillPageList(IPage<FilBill> page,@Param(Constants.WRAPPER) Wrapper queryWrapper);

}
