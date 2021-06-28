package com.mei.hui.miner.service;


import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.model.RequestMachineInfo;

import java.util.List;

/**
 * 矿机信息Service接口
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
public interface ISysMachineInfoService 
{
    /**
     * 查询矿机信息
     * 
     * @param id 矿机信息ID
     * @return 矿机信息
     */
    public SysMachineInfo selectSysMachineInfoById(Long id);

    /**
     * 查询矿机信息列表
     * 
     * @param sysMachineInfo 矿机信息
     * @return 矿机信息集合
     */
    public List<SysMachineInfo> selectSysMachineInfoList(SysMachineInfo sysMachineInfo);

    /**
     * 新增矿机信息
     * 
     * @param sysMachineInfo 矿机信息
     * @return 结果
     */
    public int insertSysMachineInfo(SysMachineInfo sysMachineInfo);

    /**
     * 修改矿机信息
     * 
     * @param sysMachineInfo 矿机信息
     * @return 结果
     */
    public int updateSysMachineInfo(SysMachineInfo sysMachineInfo);

    /**
     * 批量删除矿机信息
     * 
     * @param ids 需要删除的矿机信息ID
     * @return 结果
     */
    public int deleteSysMachineInfoByIds(Long[] ids);

    /**
     * 删除矿机信息信息
     * 
     * @param id 矿机信息ID
     * @return 结果
     */
    public int deleteSysMachineInfoById(Long id);

    SysMachineInfo selectSysMachineInfoByMinerAndHostname(String minerId, String hostname);

    Long countByMinerId(String minerId);

    /**
     * 获取N条记录
     *
     * @param offset 起始位置
     * @param rowCount 条数
     * @return 结果
     */
    List<SysMachineInfo> selectSysMachineInfoByLimit(Integer offset, Integer rowCount);

    /**
    * 批量新增矿机
    *
    * @description
    * @author shangbin
    * @date 2021/6/28 16:14
    * @param [requestMachineInfoListBO]
    * @return int
    * @version v1.4.0
    */
    public int insertSysMachineInfoList(List<RequestMachineInfo> requestMachineInfoList);
}
