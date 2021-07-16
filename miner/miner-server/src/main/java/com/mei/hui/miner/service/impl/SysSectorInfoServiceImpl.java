package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.mapper.SysSectorInfoMapper;
import com.mei.hui.miner.service.ISysSectorInfoService;
import com.mei.hui.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扇区信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Service
public class SysSectorInfoServiceImpl implements ISysSectorInfoService
{
    @Autowired
    private SysSectorInfoMapper sysSectorInfoMapper;

    /**
     * 查询扇区信息
     * 
     * @param id 扇区信息ID
     * @return 扇区信息
     */
    @Override
    public SysSectorInfo selectSysSectorInfoById(Long id)
    {
        return sysSectorInfoMapper.selectSysSectorInfoById(id);
    }

    /**
     * 查询扇区信息列表
     * 
     * @param sysSectorInfo 扇区信息
     * @return 扇区信息
     */
    @Override
    public List<SysSectorInfo> selectSysSectorInfoList(SysSectorInfo sysSectorInfo)
    {
        LambdaQueryWrapper<SysSectorInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.setEntity(sysSectorInfo);
        return sysSectorInfoMapper.selectList(queryWrapper);
    }

    public Map<String,Object> list(SysSectorInfo sysSectorInfo)
    {
        LambdaQueryWrapper<SysSectorInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.setEntity(sysSectorInfo);
        IPage page = sysSectorInfoMapper.selectPage(new Page(sysSectorInfo.getPageNum(), sysSectorInfo.getPageSize()), lambdaQueryWrapper);
        /**
         * 组装返回信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("rows", page.getRecords());
        map.put("total", page.getTotal());
        return map;
    }

    /**
     * 新增扇区信息
     * 
     * @param sysSectorInfo 扇区信息
     * @return 结果
     */
    @Override
    public int insertSysSectorInfo(SysSectorInfo sysSectorInfo)
    {
        sysSectorInfo.setCreateTime(LocalDateTime.now());
        return sysSectorInfoMapper.insert(sysSectorInfo);
    }

    /**
     * 修改扇区信息
     * 
     * @param sysSectorInfo 扇区信息
     * @return 结果
     */
    @Override
    public int updateSysSectorInfo(SysSectorInfo sysSectorInfo)
    {
        sysSectorInfo.setUpdateTime(LocalDateTime.now());
        return sysSectorInfoMapper.updateById(sysSectorInfo);
    }

    /**
     * 批量删除扇区信息
     * 
     * @param ids 需要删除的扇区信息ID
     * @return 结果
     */
    @Override
    public int deleteSysSectorInfoByIds(Long[] ids)
    {
        return sysSectorInfoMapper.deleteSysSectorInfoByIds(ids);
    }

    /**
     * 删除扇区信息信息
     * 
     * @param id 扇区信息ID
     * @return 结果
     */
    @Override
    public int deleteSysSectorInfoById(Long id)
    {
        return sysSectorInfoMapper.deleteSysSectorInfoById(id);
    }

    /**
     * 查询扇区信息是否已存在
     *
     * @param sysSectorInfo 扇区信息
     * @return 扇区信息
     */
    @Override
    public SysSectorInfo selectSysSectorInfoByMinerIdAndSectorNoAndStatus(SysSectorInfo sysSectorInfo)
    {
        return sysSectorInfoMapper.selectSysSectorInfoByMinerIdAndSectorNoAndStatus(sysSectorInfo);
    }

    /*查询数据库里该miner_id、sector_no小于传过来的sector_status的值是否有进行中的状态*/
    @Override
    public List<SysSectorInfo> selectSysSectorInfoByMinerIdAndSectorNoAndSectorAndLtStatus(SysSectorInfo sectorInfo) {
        return sysSectorInfoMapper.selectSysSectorInfoByMinerIdAndSectorNoAndSectorAndLtStatus(sectorInfo);
    }
}
