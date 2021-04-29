package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.mapper.SysAggAccountDailyMapper;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 账户按天聚合Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-04-06
 */
@Service
public class SysAggAccountDailyServiceImpl implements ISysAggAccountDailyService
{
    @Autowired
    private SysAggAccountDailyMapper sysAggAccountDailyMapper;

    /**
     * 查询账户按天聚合
     * 
     * @param id 账户按天聚合ID
     * @return 账户按天聚合
     */
    @Override
    public SysAggAccountDaily selectSysAggAccountDailyById(Long id)
    {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyById(id);
    }

    @Override
    public SysAggAccountDaily selectSysAggAccountDailyByMinerIdAndDate(String minerId, String date) {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyByMinerIdAndDate(minerId, date);
    }

    @Override
    public List<SysAggAccountDaily> selectSysAggAccountDailyByMinerId(String minerId, String begin, String end) {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyByMinerId(minerId, begin, end);
    }

    /**
     * 查询账户按天聚合列表
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 账户按天聚合
     */
    @Override
    public List<SysAggAccountDaily> selectSysAggAccountDailyList(SysAggAccountDaily sysAggAccountDaily)
    {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyList(sysAggAccountDaily);
    }

    /**
     * 新增账户按天聚合
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    @Override
    public int insertSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily)
    {
        sysAggAccountDaily.setCreateTime(LocalDateTime.now());
        return sysAggAccountDailyMapper.insertSysAggAccountDaily(sysAggAccountDaily);
    }

    /**
     * 修改账户按天聚合
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    @Override
    public int updateSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily)
    {
        sysAggAccountDaily.setUpdateTime(LocalDateTime.now());
        return sysAggAccountDailyMapper.updateSysAggAccountDaily(sysAggAccountDaily);
    }

    /**
     * 批量删除账户按天聚合
     * 
     * @param ids 需要删除的账户按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggAccountDailyByIds(Long[] ids)
    {
        return sysAggAccountDailyMapper.deleteSysAggAccountDailyByIds(ids);
    }

    /**
     * 删除账户按天聚合信息
     * 
     * @param id 账户按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggAccountDailyById(Long id)
    {
        return sysAggAccountDailyMapper.deleteSysAggAccountDailyById(id);
    }
}
