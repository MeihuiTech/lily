package com.mei.hui.miner.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 账户按天聚合Mapper接口
 * 
 * @author ruoyi
 * @date 2021-04-06
 */
@Repository
public interface SysAggAccountDailyMapper extends BaseMapper<SysAggAccountDaily>
{
    /**
     * 查询账户按天聚合
     * 
     * @param id 账户按天聚合ID
     * @return 账户按天聚合
     */
    public SysAggAccountDaily selectSysAggAccountDailyById(Long id);

    /**
     * 查询账户按天聚合列表
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 账户按天聚合集合
     */
    public List<SysAggAccountDaily> selectSysAggAccountDailyList(SysAggAccountDaily sysAggAccountDaily);

    /**
     * 新增账户按天聚合
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    public int insertSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily);

    /**
     * 修改账户按天聚合
     * 
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    public int updateSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily);

    /**
     * 删除账户按天聚合
     * 
     * @param id 账户按天聚合ID
     * @return 结果
     */
    public int deleteSysAggAccountDailyById(Long id);

    /**
     * 批量删除账户按天聚合
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysAggAccountDailyByIds(Long[] ids);

    SysAggAccountDaily selectSysAggAccountDailyByMinerIdAndDate(@Param("minerId") String minerId, @Param("date") String date);

    List<SysAggAccountDaily> selectSysAggAccountDailyByMinerId(@Param("minerId") String minerId, @Param("begin") String begin, @Param("end") String end);
}
