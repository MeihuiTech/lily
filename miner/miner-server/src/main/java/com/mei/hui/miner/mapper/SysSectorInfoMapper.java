package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.model.RequestSectorInfo;
import org.apache.ibatis.annotations.Param;
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

    /**
    * 查询数据库里该miner_id、sector_no小于传过来的sector_status的值是否有进行中的状态
    *
    * @description
    * @author shangbin
    * @date 2021/7/16 11:44
    * @param [sectorInfo]
    * @return java.util.List<com.mei.hui.miner.entity.SysSectorInfo>
    * @version v1.4.1
    */
    public List<SysSectorInfo> selectSysSectorInfoByMinerIdAndSectorNoAndSectorAndLtStatus(com.mei.hui.miner.entity.SysSectorInfo sectorInfo);

    /**
    * 封装扇区有错误，重新封装时，删除以前的旧数据
    *
    * @description
    * @author shangbin
    * @date 2021/7/22 16:00
    * @param [sysSectorInfo]
    * @return java.lang.Integer
    * @version v1.4.1
    */
    public Integer deleteSysSectorInfoOld(RequestSectorInfo sysSectorInfo);

    /**
    * 查询sys_sector_info里的所有的封装时间总和
    *
    * @description
    * @author shangbin
    * @date 2021/7/22 16:25
    * @param [minerId, sectorNo]
    * @return java.lang.Long
    * @version v1.4.1
    */
    public Long selectSysSectorInfoSumSectorDuration(@Param("minerId") String minerId,@Param("sectorNo") Long sectorNo);

    /**
    * 查询数据库里该miner_id、sector_no小于传过来的sector_status的值是否有进行中的状态，如果有，改成已完成
    *
    * @description
    * @author shangbin
    * @date 2021/7/27 10:40
    * @param [sysSectorInfo]
    * @return java.lang.Integer
    * @version v1.4.1
    */
    public Integer updateSysSectorInfoByMinerIdAndSectorNoAndSectorAndLtStatus(RequestSectorInfo sysSectorInfo);
}
