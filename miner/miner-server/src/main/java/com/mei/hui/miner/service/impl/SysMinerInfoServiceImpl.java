package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.PoolInfo;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysTotalEarning;
import com.mei.hui.miner.mapper.PoolInfoMapper;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;

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
        miner.setSectorPledge(BigDecimalUtil.formatFour(miner.getSectorPledge()));
        miner.setLockAward(BigDecimalUtil.formatFour(miner.getLockAward()));
        miner.setTotalBlockAward(BigDecimalUtil.formatFour(miner.getTotalBlockAward()));
        miner.setBalanceMinerAvailable(BigDecimalUtil.formatFour(miner.getBalanceMinerAvailable()));
        miner.setBalanceMinerAccount(BigDecimalUtil.formatFour(miner.getBalanceMinerAccount()));
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
        List<SysMinerInfo> list = null;
        if(sysMinerInfo.getUserId() == 1L){
            list = sysMinerInfoMapper.selectSysMinerInfoList(new SysMinerInfo());
        }else{
            list = sysMinerInfoMapper.selectSysMinerInfoList(sysMinerInfo);
       }
        return list;
    }

    public List<SysMinerInfo> findMinerInfoList(SysMinerInfo sysMinerInfo){
        return sysMinerInfoMapper.selectSysMinerInfoList(sysMinerInfo);
    }

    public Map<String,Object> findPage(SysMinerInfo sysMinerInfo)
    {
        Long userId = HttpRequestUtil.getUserId();
        sysMinerInfo.setUserId(userId);
        LambdaQueryWrapper<SysMinerInfo> query = new LambdaQueryWrapper<>();
        query.setEntity(sysMinerInfo);
        IPage<SysMinerInfo> page = sysMinerInfoMapper.selectPage(new Page(sysMinerInfo.getPageNum(), sysMinerInfo.getPageSize()), query);
        for (SysMinerInfo info: page.getRecords()) {
            Long c = countByMinerId(info.getMinerId());
            info.setMachineCount(c);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",page.getRecords());
        map.put("total",page.getTotal());
        return map;
    }

    public Long countByMinerId(String minerId) {
        return sysMinerInfoMapper.countByMinerId(minerId);
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


    public Map<String,Object> machines(Long id,int pageNum,int pageSize) {

        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo miner = selectSysMinerInfoById(id);
        if (miner == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        if (userId != null && userId != 1L && !userId.equals(miner.getUserId())) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
        }

        LambdaQueryWrapper<SysMachineInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysMachineInfo::getMinerId,miner.getMinerId());
        IPage<SysMachineInfo> page = sysMachineInfoMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",page.getRecords());
        map.put("total",page.getTotal());
        return map;
    }
}
