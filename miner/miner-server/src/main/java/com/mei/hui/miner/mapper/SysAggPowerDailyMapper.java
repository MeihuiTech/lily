package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 算力按天聚合Mapper接口
 * @author ruoyi
 * @date 2021-04-06
 */
@Repository
public interface SysAggPowerDailyMapper extends BaseMapper<SysAggPowerDaily>
{
    /**
     * 查询算力按天聚合
     * 
     * @param id 算力按天聚合ID
     * @return 算力按天聚合
     */
    public SysAggPowerDaily selectSysAggPowerDailyById(Long id);

    /**
     * 查询算力按天聚合列表
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 算力按天聚合集合
     */
    public List<SysAggPowerDaily> selectSysAggPowerDailyList(SysAggPowerDaily sysAggPowerDaily);

    /**
     * 新增算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    public int insertSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily);

    /**
     * 修改算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    public int updateSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily);

    /**
     * 删除算力按天聚合
     * 
     * @param id 算力按天聚合ID
     * @return 结果
     */
    public int deleteSysAggPowerDailyById(Long id);

    /**
     * 批量删除算力按天聚合
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysAggPowerDailyByIds(Long[] ids);

    public SysAggPowerDaily selectSysAggPowerDailyByMinerIdAndDate(@Param("minerId") String minerId, @Param("date") String date);

    List<SysAggPowerDaily> selectSysAggPowerDailyByMinerId(@Param("minerId") String minerId, @Param("begin") String begin, @Param("end") String end,@Param("type") String type);
}
