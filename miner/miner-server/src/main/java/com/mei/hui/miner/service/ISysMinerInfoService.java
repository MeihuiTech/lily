package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysTotalEarning;

import java.util.List;
import java.util.Map;

/**
 * 矿工信息Service接口
 *
 * @author ruoyi
 * @date 2021-03-02
 */
public interface ISysMinerInfoService
{
    /**
     * 查询矿工信息
     *
     * @param id 矿工信息ID
     * @return 矿工信息
     */
    public SysMinerInfo selectSysMinerInfoById(Long id);

    /**
     * 通过miner_id查询矿工信息
     *
     * @param miner_id 矿工miner_id
     * @return 矿工信息
     */
    public SysMinerInfo selectSysMinerInfoByMinerId(String miner_id);

    /**
     * 查询矿工信息列表
     *
     * @param sysMinerInfo 矿工信息
     * @return 矿工信息集合
     */
    public List<SysMinerInfo> selectSysMinerInfoList(SysMinerInfo sysMinerInfo);

    /**
     * 新增矿工信息
     *
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    public int insertSysMinerInfo(SysMinerInfo sysMinerInfo);

    /**
     * 修改矿工信息
     *
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    public int updateSysMinerInfo(SysMinerInfo sysMinerInfo);

    /**
     * 批量删除矿工信息
     *
     * @param ids 需要删除的矿工信息ID
     * @return 结果
     */
    public int deleteSysMinerInfoByIds(Long[] ids);

    /**
     * 删除矿工信息信息
     *
     * @param id 矿工信息ID
     * @return 结果
     */
    public int deleteSysMinerInfoById(Long id);

    SysMinerInfo selectSysMinerInfoByUserIdAndMinerId(Long userId, String minerId);

    /**
     * 获取该用户总收益和总锁仓收益
     *
     * @param userId 用户ID
     * @return 结果
     */
    SysTotalEarning selectTotalEarningAndAwardByUserId(Long userId);

    public Long countByMinerId(String minerId);

    Map<String,Object> findPage(SysMinerInfo sysMinerInfo);

    public Map<String,Object> machines(Long id,int pageNum,int pageSize);
}
