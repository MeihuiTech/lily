package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysSectorInfo;

import java.util.List;
import java.util.Map;

/**
 * 扇区信息Service接口
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
public interface ISysSectorInfoService 
{
    /**
     * 查询扇区信息
     * 
     * @param id 扇区信息ID
     * @return 扇区信息
     */
    public SysSectorInfo selectSysSectorInfoById(Long id);

    /**
     * 查询扇区信息列表
     * 
     * @param sysSectorInfo 扇区信息
     * @return 扇区信息集合
     */
    public List<SysSectorInfo> selectSysSectorInfoList(SysSectorInfo sysSectorInfo);

    /**
     * 新增扇区信息
     * 
     * @param sysSectorInfo 扇区信息
     * @return 结果
     */
    public int insertSysSectorInfo(SysSectorInfo sysSectorInfo);

    /**
     * 修改扇区信息
     * 
     * @param sysSectorInfo 扇区信息
     * @return 结果
     */
    public int updateSysSectorInfo(SysSectorInfo sysSectorInfo);

    /**
     * 批量删除扇区信息
     * 
     * @param ids 需要删除的扇区信息ID
     * @return 结果
     */
    public int deleteSysSectorInfoByIds(Long[] ids);

    /**
     * 删除扇区信息信息
     * 
     * @param id 扇区信息ID
     * @return 结果
     */
    public int deleteSysSectorInfoById(Long id);

    /**
     * 查询扇区封装记录表 sys_sector_info里的扇区信息是否已存在
     *
     * @param sysSectorInfo 扇区信息
     * @return 结果
     */
    SysSectorInfo selectSysSectorInfoByMinerIdAndSectorNoAndStatus(SysSectorInfo sysSectorInfo);

    public Map<String,Object> list(SysSectorInfo sysSectorInfo);
}
