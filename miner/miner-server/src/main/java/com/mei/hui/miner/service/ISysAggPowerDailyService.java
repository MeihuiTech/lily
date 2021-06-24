package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.model.PowerAvailableFilVO;

import java.util.List;

/**
 * 算力按天聚合Service接口
 * @author ruoyi
 * @date 2021-04-06
 */
public interface ISysAggPowerDailyService 
{
    /**
     * 查询算力按天聚合
     * 
     * @param id 算力按天聚合ID
     * @return 算力按天聚合
     */
    public SysAggPowerDaily selectSysAggPowerDailyById(Long id);

    /**
     * 查询算力按天聚合列表
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 算力按天聚合集合
     */
    public List<SysAggPowerDaily> selectSysAggPowerDailyList(SysAggPowerDaily sysAggPowerDaily);

    /**
     * 新增算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    public int insertSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily);

    /**
     * 修改算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    public int updateSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily);

    /**
     * 批量删除算力按天聚合
     * 
     * @param ids 需要删除的算力按天聚合ID
     * @return 结果
     */
    public int deleteSysAggPowerDailyByIds(Long[] ids);

    /**
     * 删除算力按天聚合信息
     * 
     * @param id 算力按天聚合ID
     * @return 结果
     */
    public int deleteSysAggPowerDailyById(Long id);

    public SysAggPowerDaily selectSysAggPowerDailyByMinerIdAndDate(String minerId, String date);

    List<SysAggPowerDaily> selectSysAggAccountDailyByMinerId(String minerId, String begin, String end,String type);
    
    /**
    * 根据算力按天聚合表实体查询出算力按天聚合表实体的list
    * 
    * @description 
    * @author shangbin
    * @date 2021/6/8 10:40
    * @param [sysAggPowerDaily] 
    * @return java.util.List<com.mei.hui.miner.entity.SysAggPowerDaily> 
    * @version v1.0.0
    */
    public List<SysAggPowerDaily> selectSysAggPowerDailyListBySysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily);

    /**
    * 管理员-首页-平台有效算力排行榜-查询算力按天聚合表里的挖矿效率、算力增速
    *
    * @description
    * @author shangbin
    * @date 2021/6/8 15:32
    * @param [yesterDayDate, minerIdList]
    * @return com.mei.hui.miner.model.PowerAvailableFilVO
    * @version v1.0.0
    */
    public PowerAvailableFilVO selectPowerAvailableByDateAndUserIdList(String yesterDayDate, List<String> minerIdList,String type);

    /**
    * 查询算力按天聚合表里昨天所有的累计出块份数
    *
    * @description
    * @author shangbin
    * @date 2021/6/24 11:01
    * @param [yesterDayDate, name]
    * @return java.lang.Long
    * @version v1.4.0
    */
    public Long selectTotalBlocksByDate(String yesterDayDate, String type, String minerId);
}
