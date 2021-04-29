package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysAggPowerDaily;

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

    List<SysAggPowerDaily> selectSysAggAccountDailyByMinerId(String minerId, String begin, String end);
}
