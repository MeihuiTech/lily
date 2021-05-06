package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SysSectorsWrap;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 扇区信息聚合Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Repository
public interface SysSectorsWrapMapper extends BaseMapper<SysSectorsWrap>
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
     * 删除扇区信息聚合
     * 
     * @param id 扇区信息聚合ID
     * @return 结果
     */
    public int deleteSysSectorsWrapById(Long id);

    /**
     * 批量删除扇区信息聚合
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysSectorsWrapByIds(Long[] ids);

    List<SysSectorsWrap> selectSysSectorsWrapListByUserId(SysSectorsWrap sysSectorsWrap);

    SysSectorsWrap selectSysSectorsWrapByMinerIdAndSectorNo(SysSectorsWrap sysSectorsWrap);
}
