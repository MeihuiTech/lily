package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.feign.vo.UserMinerBO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 矿机信息Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
@Repository
public interface SysMachineInfoMapper  extends BaseMapper<SysMachineInfo>
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
     * 删除矿机信息
     * 
     * @param id 矿机信息ID
     * @return 结果
     */
    public int deleteSysMachineInfoById(Long id);

    /**
     * 批量删除矿机信息
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysMachineInfoByIds(Long[] ids);

    SysMachineInfo selectSysMachineInfoByMinerAndHostname(@Param("minerId") String minerId, @Param("hostname") String hostname);

    Long countByMinerId(@Param("minerId") String minerId);

    /**
     * 获取N条记录
     *
     * @param offset 起始位置
     * @param rowCount 条数
     * @return 结果
     */
    List<SysMachineInfo> selectSysMachineInfoByLimit(@Param("offset") Integer offset, @Param("rowCount") Integer rowCount);

}
