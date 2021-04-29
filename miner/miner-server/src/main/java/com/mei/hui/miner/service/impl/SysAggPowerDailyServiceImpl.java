package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.mapper.SysAggPowerDailyMapper;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 算力按天聚合Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-04-06
 */
@Service
public class SysAggPowerDailyServiceImpl implements ISysAggPowerDailyService
{
    @Autowired
    private SysAggPowerDailyMapper sysAggPowerDailyMapper;

    /**
     * 查询算力按天聚合
     * 
     * @param id 算力按天聚合ID
     * @return 算力按天聚合
     */
    @Override
    public SysAggPowerDaily selectSysAggPowerDailyById(Long id)
    {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyById(id);
    }

    /**
     * 查询算力按天聚合列表
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 算力按天聚合
     */
    @Override
    public List<SysAggPowerDaily> selectSysAggPowerDailyList(SysAggPowerDaily sysAggPowerDaily)
    {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyList(sysAggPowerDaily);
    }

    /**
     * 新增算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    @Override
    public int insertSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily)
    {
        sysAggPowerDaily.setCreateTime(LocalDateTime.now());
        return sysAggPowerDailyMapper.insertSysAggPowerDaily(sysAggPowerDaily);
    }

    /**
     * 修改算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    @Override
    public int updateSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily)
    {
        sysAggPowerDaily.setUpdateTime(LocalDateTime.now());
        return sysAggPowerDailyMapper.updateSysAggPowerDaily(sysAggPowerDaily);
    }

    /**
     * 批量删除算力按天聚合
     * 
     * @param ids 需要删除的算力按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggPowerDailyByIds(Long[] ids)
    {
        return sysAggPowerDailyMapper.deleteSysAggPowerDailyByIds(ids);
    }

    /**
     * 删除算力按天聚合信息
     * 
     * @param id 算力按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggPowerDailyById(Long id)
    {
        return sysAggPowerDailyMapper.deleteSysAggPowerDailyById(id);
    }

    @Override
    public SysAggPowerDaily selectSysAggPowerDailyByMinerIdAndDate(String minerId, String date) {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyByMinerIdAndDate(minerId, date);
    }

    @Override
    public List<SysAggPowerDaily> selectSysAggAccountDailyByMinerId(String minerId, String begin, String end) {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyByMinerId(minerId, begin, end);
    }
}
