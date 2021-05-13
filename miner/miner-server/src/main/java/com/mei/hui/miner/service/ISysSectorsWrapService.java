package com.mei.hui.miner.service;


import com.mei.hui.miner.entity.SysSectorsWrap;
import com.mei.hui.miner.model.RequestSectorInfo;

import java.util.List;
import java.util.Map;

/**
 * 扇区信息聚合Service接口
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
public interface ISysSectorsWrapService 
{
    /**
     * 查询扇区信息聚合
     * 
     * @param id 扇区信息聚合ID
     * @return 扇区信息聚合
     */
    public SysSectorsWrap selectSysSectorsWrapById(Long id);

    /**
     * 查询扇区信息聚合列表
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 扇区信息聚合集合
     */
    public List<SysSectorsWrap> selectSysSectorsWrapList(SysSectorsWrap sysSectorsWrap);

    /**
     * 新增扇区信息聚合
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    public int insertSysSectorsWrap(SysSectorsWrap sysSectorsWrap);

    /**
     * 修改扇区信息聚合
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    public int updateSysSectorsWrap(SysSectorsWrap sysSectorsWrap);

    /**
     * 批量删除扇区信息聚合
     * 
     * @param ids 需要删除的扇区信息聚合ID
     * @return 结果
     */
    public int deleteSysSectorsWrapByIds(Long[] ids);

    /**
     * 删除扇区信息聚合信息
     * 
     * @param id 扇区信息聚合ID
     * @return 结果
     */
    public int deleteSysSectorsWrapById(Long id);

    List<SysSectorsWrap> selectSysSectorsWrapListByUserId(SysSectorsWrap sysSectorsWrap, Long userId);


    /**
     * 查询该扇区聚合信息是否已存在
     *
     * @param sysSectorsWrap 扇区信息聚合
     * @return 扇区信息聚合集合
     */
    public SysSectorsWrap selectSysSectorsWrapByMinerIdAndSectorNo(SysSectorsWrap sysSectorsWrap);

    public Map<String,Object> list(SysSectorsWrap sysSectorsWrap);

    /*
    *
    * @description 新增扇区信息
    * @author shangbin
    * @date 2021/5/12 14:46
    * @param [sysSectorInfo]
    * @return int
    * @version v1.0.0
    */
    public int addSector(RequestSectorInfo sysSectorInfo);

    public int testInsert(SysSectorsWrap sysSectorsWrap);
}
