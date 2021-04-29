package com.mei.hui.miner.service.impl;

import com.mei.hui.miner.entity.PoolInfo;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysTotalEarning;
import com.mei.hui.miner.mapper.PoolInfoMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.ISysMinerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 矿工信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
@Service
public class SysMinerInfoServiceImpl implements ISysMinerInfoService
{
    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;

    @Autowired
    PoolInfoMapper poolInfoMapper;

    /**
     * 查询矿工信息
     * @param id 矿工信息ID
     * @return 矿工信息
     */
    @Override
    public SysMinerInfo selectSysMinerInfoById(Long id)
    {
        SysMinerInfo miner = sysMinerInfoMapper.selectSysMinerInfoById(id);
        if (miner == null) {
            return null;
        }
        PoolInfo machine = poolInfoMapper.selectMachineInfoByUserIdAndMinerId(miner.getUserId(),miner.getMinerId());
        if (machine != null) {
            miner.setWorkerCount(machine.getWorkerCount());
        }
        return miner;
    }

    /**
     * 通过miner_id查询矿工信息
     *
     * @param miner_id 矿工miner_id
     * @return 矿工信息
     */
    @Override
    public SysMinerInfo selectSysMinerInfoByMinerId(String miner_id) {
        return sysMinerInfoMapper.selectSysMinerInfoByMinerId(miner_id);
    }

    /**
     * 查询矿工信息列表
     * 
     * @param sysMinerInfo 矿工信息
     * @return 矿工信息
     */
    @Override
    public List<SysMinerInfo> selectSysMinerInfoList(SysMinerInfo sysMinerInfo)
    {
        return sysMinerInfoMapper.selectSysMinerInfoList(sysMinerInfo);
    }

    /**
     * 新增矿工信息
     * 
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    @Override
    public int insertSysMinerInfo(SysMinerInfo sysMinerInfo)
    {
        sysMinerInfo.setCreateTime(LocalDateTime.now());
        return sysMinerInfoMapper.insertSysMinerInfo(sysMinerInfo);
    }

    /**
     * 修改矿工信息
     * 
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    @Override
    public int updateSysMinerInfo(SysMinerInfo sysMinerInfo)
    {
        sysMinerInfo.setUpdateTime(LocalDateTime.now());
        return sysMinerInfoMapper.updateSysMinerInfo(sysMinerInfo);
    }

    /**
     * 批量删除矿工信息
     * 
     * @param ids 需要删除的矿工信息ID
     * @return 结果
     */
    @Override
    public int deleteSysMinerInfoByIds(Long[] ids)
    {
        return sysMinerInfoMapper.deleteSysMinerInfoByIds(ids);
    }

    /**
     * 删除矿工信息信息
     * 
     * @param id 矿工信息ID
     * @return 结果
     */
    @Override
    public int deleteSysMinerInfoById(Long id)
    {
        return sysMinerInfoMapper.deleteSysMinerInfoById(id);
    }

    @Override
    public SysMinerInfo selectSysMinerInfoByUserIdAndMinerId(Long userId, String minerId) {
        return sysMinerInfoMapper.selectSysMinerInfoByUserIdAndMinerId(userId, minerId);
    }

    /**
     * 获取该用户总收益和总锁仓收益
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public SysTotalEarning selectTotalEarningAndAwardByUserId(Long userId) {
        return sysMinerInfoMapper.selectTotalEarningAndAwardByUserId(userId);
    }
}
