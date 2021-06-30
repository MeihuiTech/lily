package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import com.mei.hui.miner.model.RequestMachineInfo;
import com.mei.hui.miner.service.ISysMachineInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 矿机信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
@Service
public class SysMachineInfoServiceImpl implements ISysMachineInfoService
{
    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;

    /**
     * 查询矿机信息
     * 
     * @param id 矿机信息ID
     * @return 矿机信息
     */
    @Override
    public SysMachineInfo selectSysMachineInfoById(Long id)
    {
        return sysMachineInfoMapper.selectSysMachineInfoById(id);
    }

    /**
     * 查询矿机信息列表
     * 
     * @param sysMachineInfo 矿机信息
     * @return 矿机信息
     */
    @Override
    public List<SysMachineInfo> selectSysMachineInfoList(SysMachineInfo sysMachineInfo)
    {
        return sysMachineInfoMapper.selectSysMachineInfoList(sysMachineInfo);
    }

    /**
     * 新增矿机信息
     * 
     * @param sysMachineInfo 矿机信息
     * @return 结果
     */
    @Override
    public int insertSysMachineInfo(SysMachineInfo sysMachineInfo)
    {
        sysMachineInfo.setCreateTime(LocalDateTime.now());
        return sysMachineInfoMapper.insertSysMachineInfo(sysMachineInfo);
    }

    /**
     * 修改矿机信息
     * 
     * @param sysMachineInfo 矿机信息
     * @return 结果
     */
    @Override
    public int updateSysMachineInfo(SysMachineInfo sysMachineInfo)
    {
        sysMachineInfo.setUpdateTime(LocalDateTime.now());
        return sysMachineInfoMapper.updateSysMachineInfo(sysMachineInfo);
    }

    /**
     * 批量删除矿机信息
     * 
     * @param ids 需要删除的矿机信息ID
     * @return 结果
     */
    @Override
    public int deleteSysMachineInfoByIds(Long[] ids)
    {
        return sysMachineInfoMapper.deleteSysMachineInfoByIds(ids);
    }

    /**
     * 删除矿机信息信息
     * 
     * @param id 矿机信息ID
     * @return 结果
     */
    @Override
    public int deleteSysMachineInfoById(Long id)
    {
        return sysMachineInfoMapper.deleteSysMachineInfoById(id);
    }

    @Override
    public SysMachineInfo selectSysMachineInfoByMinerAndHostname(String minerId, String hostname) {
        return sysMachineInfoMapper.selectSysMachineInfoByMinerAndHostname(minerId,hostname);
    }

    @Override
    public Long countByMinerId(String minerId) {
        return sysMachineInfoMapper.countByMinerId(minerId);
    }

    /**
     * 获取N条记录
     * @param offset 起始位置
     * @param rowCount 条数
     * @return 结果
     */
    @Override
    public List<SysMachineInfo> selectSysMachineInfoByLimit(Integer offset, Integer rowCount) {
        return sysMachineInfoMapper.selectSysMachineInfoByLimit(offset, rowCount);
    }

    /*批量新增矿机*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertSysMachineInfoList(List<RequestMachineInfo> requestMachineInfoList) {
        int count = 0;
        for (RequestMachineInfo requestMachineInfo: requestMachineInfoList) {
            SysMachineInfo machine = selectSysMachineInfoByMinerAndHostname(requestMachineInfo.getMinerId(),requestMachineInfo.getHostname());
            requestMachineInfo.setOnline(1);
            if (machine == null) {
                int rows = insertSysMachineInfo(requestMachineInfo);
                count += rows;
            } else {
                requestMachineInfo.setId(machine.getId());
                int rows = updateSysMachineInfo(requestMachineInfo);
                count += rows;
            }
        }


        return count;
    }
}
