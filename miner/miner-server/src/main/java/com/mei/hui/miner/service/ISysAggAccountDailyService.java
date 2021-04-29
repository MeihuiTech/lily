package com.mei.hui.miner.service;


import com.mei.hui.miner.entity.SysAggAccountDaily;

import java.util.List;

/**
 * 账户按天聚合Service接口
 * 
 * @author ruoyi
 * @date 2021-04-06
 */
public interface ISysAggAccountDailyService 
{
    /**
     * 查询账户按天聚合
     * 
     * @param id 账户按天聚合ID
     * @return 账户按天聚合
     */
    public SysAggAccountDaily selectSysAggAccountDailyById(Long id);

    public SysAggAccountDaily selectSysAggAccountDailyByMinerIdAndDate(String minerId, String date);

    public List<SysAggAccountDaily> selectSysAggAccountDailyByMinerId(String minerId, String begin, String end);

    /**
     * 查询账户按天聚合列表
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 账户按天聚合集合
     */
    public List<SysAggAccountDaily> selectSysAggAccountDailyList(SysAggAccountDaily sysAggAccountDaily);

    /**
     * 新增账户按天聚合
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    public int insertSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily);

    /**
     * 修改账户按天聚合
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    public int updateSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily);

    /**
     * 批量删除账户按天聚合
     * 
     * @param ids 需要删除的账户按天聚合ID
     * @return 结果
     */
    public int deleteSysAggAccountDailyByIds(Long[] ids);

    /**
     * 删除账户按天聚合信息
     * 
     * @param id 账户按天聚合ID
     * @return 结果
     */
    public int deleteSysAggAccountDailyById(Long id);
}
