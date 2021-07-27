package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.model.RequestSectorInfo;

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

    /**
    * 查询数据库里该miner_id、sector_no小于传过来的sector_status的值是否有进行中的状态
    *
    * @description
    * @author shangbin
    * @date 2021/7/16 11:43
    * @param [sectorInfo]
    * @return java.util.List<com.mei.hui.miner.entity.SysSectorInfo>
    * @version v1.4.1
    */
    public List<SysSectorInfo> selectSysSectorInfoByMinerIdAndSectorNoAndSectorAndLtStatus(com.mei.hui.miner.entity.SysSectorInfo sectorInfo);

    /**
    * 查询数据库里该miner_id、sector_no小于传过来的sector_status的值是否有进行中的状态，如果有，改成已完成
    *
    * @description
    * @author shangbin
    * @date 2021/7/27 10:38
    * @param [sysSectorInfo]
    * @return java.lang.Integer
    * @version v1.4.1
    */
    public Integer updateSysSectorInfoByMinerIdAndSectorNoAndSectorAndLtStatus(RequestSectorInfo sysSectorInfo);
}
