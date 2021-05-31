package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysTotalEarning;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.model.XchMinerDetailBO;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 矿工信息Service接口
 *
 * @author ruoyi
 * @date 2021-03-02
 */
public interface ISysMinerInfoService
{
    /**
     * 获取起亚币
     * @param id
     * @return
     */
    XchMinerDetailBO getXchMinerById(Long id);
    /**
     * 查询矿工信息
     *
     * @param id 矿工信息ID
     * @return 矿工信息
     */
    public SysMinerInfo selectSysMinerInfoById(Long id);

    /**
     * 通过miner_id查询矿工信息
     *
     * @param miner_id 矿工miner_id
     * @return 矿工信息
     */
    public SysMinerInfo selectSysMinerInfoByMinerId(String miner_id);

    /**
     * 查询矿工信息列表
     *
     * @param sysMinerInfo 矿工信息
     * @return 矿工信息集合
     */
    public List<SysMinerInfo> selectSysMinerInfoList(SysMinerInfo sysMinerInfo);

    /**
     * 新增矿工信息
     *
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    public int insertSysMinerInfo(SysMinerInfo sysMinerInfo);

    /**
     * 修改矿工信息
     *
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    public int updateSysMinerInfo(SysMinerInfo sysMinerInfo);

    /**
     * 批量删除矿工信息
     *
     * @param ids 需要删除的矿工信息ID
     * @return 结果
     */
    public int deleteSysMinerInfoByIds(Long[] ids);

    SysMinerInfo selectSysMinerInfoByUserIdAndMinerId(Long userId, String minerId);


    public Long countByMinerId(String minerId);

    Map<String,Object> findPage(SysMinerInfoBO sysMinerInfoBO);

    public Map<String,Object> machines(Long id,int pageNum,int pageSize);

    public List<SysMinerInfo> findMinerInfoList(SysMinerInfo sysMinerInfo);

    /**
     * 通过userid 集合批量获取旷工
     */
    Result<List<AggMinerVO>> findBatchMinerByUserId(List<Long> userIds);

    /**
     * 获取起亚币旷工列表
     * @return
     */
    List<SysMinerInfo> findXchMinerList();

    /**
     * 获取fil币算力
     * @param id
     * @return
     */
    Map<String,Object> dailyPower(Long id);

    /**
     * 获取起亚币币聚合信息
     * @param id
     * @return
     */
    Map<String,Object> chiaDailyPower(Long id);

    /**
     * 获取 fil 币 收益增长列表
     * @param id
     * @return
     */
    PageResult dailyAccount(Long id);

    /**
     * 获取 chia 币 收益增长列表
     * @param id
     * @return
     */
    PageResult chiaDailyAccount(Long id);

    /**
     * fil管理员首页-旷工统计数据-当天出块份数
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 15:37
     * @param [yesterDay]
     * @return void
     * @version v1.0.0
     */
    public Long selectFilAllBlocksPerDay();

    /**
     * fil管理员首页-旷工统计数据-平台总资产
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:04
     * @param []
     * @return java.math.BigDecimal
     * @version v1.0.0
     */
    public BigDecimal selectFilAllBalanceMinerAccount();

    /**
     * fil管理员首页-旷工统计数据-平台有效算力
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:16
     * @param []
     * @return java.math.BigDecimal
     * @version v1.0.0
     */
    public BigDecimal selectFilAllPowerAvailable();

    /**
     * fil管理员首页-旷工统计数据-活跃旷工
     *
     * @description
     * @author shangbin
     * @date 2021/5/28 17:22
     * @param []
     * @return java.lang.Long
     * @version v1.0.0
     */
    public Long selectFilAllMinerIdCount();

}
