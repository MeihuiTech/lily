package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.feign.vo.UserMinerBO;
import com.mei.hui.miner.model.PowerAvailableFilVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ChiaMinerMapper extends BaseMapper<ChiaMiner> {


    /**
     * chia管理员首页-旷工统计数据-平台总资产
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:05
     * @param []
     * @return java.math.BigDecimal
     * @version v1.0.0
     */
    public BigDecimal selectAllBalanceMinerAccount();

    /**
     * chia管理员首页-旷工统计数据-平台有效算力
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:16
     * @param []
     * @return java.math.BigDecimal
     * @version v1.0.0
     */
    public BigDecimal selectAllPowerAvailable();

    /**
     * chia管理员首页-旷工统计数据-活跃旷工
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:22
     * @param []
     * @return java.lang.Long
     * @version v1.0.0
     */
    public Long selectAllMinerIdCount();

    /**
     * chia币矿工信息表里所有的当天出块份数
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 15:58
     * @param []
     * @return java.math.BigDecimal
     * @version v1.0.0
     */
    public Long selectAllBlocksPerDay(@Param("yesterDayDate") String yesterDayDate);

    /**
     * chia币管理员首页-平台有效算力排行榜
     *
     * @description
     * @author shangbin
     * @date 2021/5/29 14:19
     * @param [yesterDayDate, allPowerAvailable]
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.mei.hui.miner.model.PowerAvailableFilVO>
     * @version v1.0.0
     */
    public IPage<PowerAvailableFilVO> powerAvailablePage(Page<PowerAvailableFilVO> powerAvailableFilVOPage, @Param("yesterDayDate") String yesterDayDate, @Param("allPowerAvailable") BigDecimal allPowerAvailable);

    /**
    * 通过userid集合批量获取旷工总算力、总收益、费率
    *
    * @description
    * @author shangbin
    * @date 2021/6/7 14:55
    * @param [userChiaMinerBO]
    * @return java.util.List<com.mei.hui.miner.feign.vo.AggMinerVO>
    * @version v1.0.0
    */
    public List<AggMinerVO> findBatchChiaMinerByUserId(UserMinerBO userMinerBO);
}
