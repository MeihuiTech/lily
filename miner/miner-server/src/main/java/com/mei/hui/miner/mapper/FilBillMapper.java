package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.feign.vo.FilBillMethodBO;
import com.mei.hui.miner.feign.vo.FilBillVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 账单方法下拉列表
     * @param filBillMethodBO
     * @return
     */
    public List<String> selectFilBillMethodList(FilBillMethodBO filBillMethodBO,@Param("startDate") String startDate,@Param("endDate") String endDate);

    public List<String> selectFilBillMethodList(@Param("minerId") String minerId,@Param("subAccount") String subAccount, @Param("startDate") String startDate,@Param("endDate")  String endDate);

    /**
    * 分页查询账单消息列表
    *
    * @description
    * @author shangbin
    * @date 2021/8/6 11:02
    * @param [objectPage, minerId, method, sender, receiver, startDate, endDate]
    * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.feign.vo.FilBillVO>
    * @version v1.4.1
    */
    public IPage<FilBillVO> selectFilBillPage(Page<FilBillVO> page,@Param("minerId") String minerId,@Param("method") String method,
                                              @Param("type") Integer type,@Param("subAccount") String subAccount,@Param("startDate") String startDate,@Param("endDate") String endDate);


}
