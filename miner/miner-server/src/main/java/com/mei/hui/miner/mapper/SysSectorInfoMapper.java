package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SysSectorInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 扇区信息Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Repository
public interface SysSectorInfoMapper extends BaseMapper<SysSectorInfo>
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
//    public int insertSysSectorInfo(SysSectorInfo sysSectorInfo);

    /**
     * 修改扇区信息
     * 
     * @param sysSectorInfo 扇区信息
     * @return 结果
     */
//    public int updateSysSectorInfo(SysSectorInfo sysSectorInfo);

    /**
     * 删除扇区信息
     * 
     * @param id 扇区信息ID
     * @return 结果
     */
    public int deleteSysSectorInfoById(Long id);

    /**
     * 批量删除扇区信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysSectorInfoByIds(Long[] ids);

    /**
     * 查询扇区信息是否已存在
     *
     * @param sysSectorInfo 扇区信息
     * @return 扇区信息
     */
    SysSectorInfo selectSysSectorInfoByMinerIdAndSectorNoAndStatus(SysSectorInfo sysSectorInfo);
}
