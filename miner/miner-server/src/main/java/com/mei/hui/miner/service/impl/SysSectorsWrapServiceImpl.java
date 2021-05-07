package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.entity.SysSectorsWrap;
import com.mei.hui.miner.mapper.SysSectorsWrapMapper;
import com.mei.hui.miner.service.ISysSectorsWrapService;
import com.mei.hui.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扇区信息聚合Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Service
public class SysSectorsWrapServiceImpl implements ISysSectorsWrapService
{
    @Autowired
    private SysSectorsWrapMapper sysSectorsWrapMapper;

    /**
     * 查询扇区信息聚合
     * 
     * @param id 扇区信息聚合ID
     * @return 扇区信息聚合
     */
    @Override
    public SysSectorsWrap selectSysSectorsWrapById(Long id)
    {
        return sysSectorsWrapMapper.selectSysSectorsWrapById(id);
    }

    /**
     * 查询扇区信息聚合列表
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 扇区信息聚合
     */
    @Override
    public List<SysSectorsWrap> selectSysSectorsWrapList(SysSectorsWrap sysSectorsWrap)
    {
        return sysSectorsWrapMapper.selectSysSectorsWrapList(sysSectorsWrap);
    }

    /**
     * 新增扇区信息聚合
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    @Override
    public int insertSysSectorsWrap(SysSectorsWrap sysSectorsWrap)
    {
        sysSectorsWrap.setCreateTime(LocalDateTime.now());
        return sysSectorsWrapMapper.insertSysSectorsWrap(sysSectorsWrap);
    }

    /**
     * 修改扇区信息聚合
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    @Override
    public int updateSysSectorsWrap(SysSectorsWrap sysSectorsWrap)
    {
        sysSectorsWrap.setUpdateTime(LocalDateTime.now());
        return sysSectorsWrapMapper.updateSysSectorsWrap(sysSectorsWrap);
    }

    /**
     * 批量删除扇区信息聚合
     * 
     * @param ids 需要删除的扇区信息聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysSectorsWrapByIds(Long[] ids)
    {
        return sysSectorsWrapMapper.deleteSysSectorsWrapByIds(ids);
    }

    /**
     * 删除扇区信息聚合信息
     * 
     * @param id 扇区信息聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysSectorsWrapById(Long id)
    {
        return sysSectorsWrapMapper.deleteSysSectorsWrapById(id);
    }

    @Override
    public List<SysSectorsWrap> selectSysSectorsWrapListByUserId(SysSectorsWrap sysSectorsWrap, Long userId) {
        //sysSectorsWrap.getParams().put("userId", userId);
        userId = HttpRequestUtil.getUserId();
        return sysSectorsWrapMapper.selectSysSectorsWrapListByUserId(sysSectorsWrap);
    }

    public Map<String,Object> list(SysSectorsWrap sysSectorsWrap) {
        Long userId = HttpRequestUtil.getUserId();
        sysSectorsWrap.getParams().put("userId", userId);
        PageHelper.startPage(Integer.valueOf(sysSectorsWrap.getPageNum()+""),Integer.valueOf(sysSectorsWrap.getPageSize()+""));
        List<SysSectorsWrap> list = sysSectorsWrapMapper.selectSysSectorsWrapListByUserId(sysSectorsWrap);
        /**
         * 组装返回信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("rows",list);
        map.put("total",new PageInfo(list).getTotal());
        return map;
    }

    /**
     * 查询该扇区聚合信息是否已存在
     *
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    @Override
    public SysSectorsWrap selectSysSectorsWrapByMinerIdAndSectorNo(SysSectorsWrap sysSectorsWrap) {
        return sysSectorsWrapMapper.selectSysSectorsWrapByMinerIdAndSectorNo(sysSectorsWrap);
    }
}
