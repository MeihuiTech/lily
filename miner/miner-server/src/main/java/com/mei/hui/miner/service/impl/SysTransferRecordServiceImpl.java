package com.mei.hui.miner.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.miner.entity.SysTransferRecord;
import com.mei.hui.miner.entity.SysTransferRecordUserName;
import com.mei.hui.miner.mapper.SysTransferRecordMapper;
import com.mei.hui.miner.service.ISysTransferRecordService;
import com.mei.hui.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统划转记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Service
public class SysTransferRecordServiceImpl implements ISysTransferRecordService
{
    @Autowired
    private SysTransferRecordMapper sysTransferRecordMapper;

    /**
     * 查询系统划转记录
     * 
     * @param id 系统划转记录ID
     * @return 系统划转记录
     */
    @Override
    public SysTransferRecord selectSysTransferRecordById(Long id)
    {
        return sysTransferRecordMapper.selectSysTransferRecordById(id);
    }

    /**
     * 查询系统划转记录列表
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录
     */
    @Override
    public List<SysTransferRecord> selectSysTransferRecordList(SysTransferRecord sysTransferRecord)
    {
        return sysTransferRecordMapper.selectSysTransferRecordList(sysTransferRecord);
    }

    /**
     * 新增系统划转记录
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    @Override
    public int insertSysTransferRecord(SysTransferRecord sysTransferRecord)
    {
        sysTransferRecord.setCreateTime(LocalDateTime.now());
        return sysTransferRecordMapper.insertSysTransferRecord(sysTransferRecord);
    }

    /**
     * 修改系统划转记录
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    @Override
    public int updateSysTransferRecord(SysTransferRecord sysTransferRecord)
    {
        sysTransferRecord.setUpdateTime(LocalDateTime.now());
        return sysTransferRecordMapper.updateSysTransferRecord(sysTransferRecord);
    }

    /**
     * 批量删除系统划转记录
     * 
     * @param ids 需要删除的系统划转记录ID
     * @return 结果
     */
    @Override
    public int deleteSysTransferRecordByIds(Long[] ids)
    {
        return sysTransferRecordMapper.deleteSysTransferRecordByIds(ids);
    }

    /**
     * 删除系统划转记录信息
     * 
     * @param id 系统划转记录ID
     * @return 结果
     */
    @Override
    public int deleteSysTransferRecordById(Long id)
    {
        return sysTransferRecordMapper.deleteSysTransferRecordById(id);
    }

    /**
     * 获取用户已提取收益
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public Double selectTotalWithdrawByUserId(Long userId)
    {
        return sysTransferRecordMapper.selectTotalWithdrawByUserId(userId);
    }

    @Override
    public BigDecimal selectTotalEarning() {
        return sysTransferRecordMapper.selectTotalEarning();
    }

    @Override
    public BigDecimal selectTodayEarning() {
        return sysTransferRecordMapper.selectTodayEarning();
    }

    /**
     * 查询系统划转记录列表,加UserName
     *
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录集合
     */
    @Override
    public Map<String,Object> selectSysTransferRecordListUserName(SysTransferRecord sysTransferRecord){
        PageHelper.startPage(Integer.valueOf(sysTransferRecord.getPageNum()+""),Integer.valueOf(sysTransferRecord.getPageSize()+""));
        List<SysTransferRecordUserName> list = sysTransferRecordMapper.selectSysTransferRecordListUserName(sysTransferRecord);
        PageInfo<SysTransferRecordUserName> pageInfo = new PageInfo<>(list);

        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("rows", list);
        map.put("total", pageInfo.getTotal());
        return map;

    }
}
