package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.SysAggPowerDailyMapper;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.model.PowerAvailableFilVO;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.CurrencyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 算力按天聚合Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-04-06
 */
@Service
@Slf4j
public class SysAggPowerDailyServiceImpl implements ISysAggPowerDailyService
{
    @Autowired
    private SysAggPowerDailyMapper sysAggPowerDailyMapper;
    @Autowired
    private ISysMinerInfoService minerInfoService;

    /**
     * 查询算力按天聚合
     * 
     * @param id 算力按天聚合ID
     * @return 算力按天聚合
     */
    @Override
    public SysAggPowerDaily selectSysAggPowerDailyById(Long id)
    {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyById(id);
    }

    /**
     * 查询算力按天聚合列表
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 算力按天聚合
     */
    @Override
    public List<SysAggPowerDaily> selectSysAggPowerDailyList(SysAggPowerDaily sysAggPowerDaily)
    {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyList(sysAggPowerDaily);
    }

    /**
     * 新增算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    @Override
    public int insertSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily)
    {
        sysAggPowerDaily.setCreateTime(LocalDateTime.now());
        return sysAggPowerDailyMapper.insertSysAggPowerDaily(sysAggPowerDaily);
    }

    /**
     * 修改算力按天聚合
     * 
     * @param sysAggPowerDaily 算力按天聚合
     * @return 结果
     */
    @Override
    public int updateSysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily)
    {
        sysAggPowerDaily.setUpdateTime(LocalDateTime.now());
        return sysAggPowerDailyMapper.updateSysAggPowerDaily(sysAggPowerDaily);
    }

    /**
     * 批量删除算力按天聚合
     * 
     * @param ids 需要删除的算力按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggPowerDailyByIds(Long[] ids)
    {
        return sysAggPowerDailyMapper.deleteSysAggPowerDailyByIds(ids);
    }

    /**
     * 删除算力按天聚合信息
     * 
     * @param id 算力按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggPowerDailyById(Long id)
    {
        return sysAggPowerDailyMapper.deleteSysAggPowerDailyById(id);
    }

    @Override
    public SysAggPowerDaily selectSysAggPowerDailyByMinerIdAndDate(String minerId, String date) {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyByMinerIdAndDate(minerId, date);
    }

    @Override
    public List<SysAggPowerDaily> selectSysAggAccountDailyByMinerId(String minerId, String begin, String end,String type) {
        return sysAggPowerDailyMapper.selectSysAggPowerDailyByMinerId(minerId, begin, end,type);
    }

    /**
     * 根据算力按天聚合表实体查询出算力按天聚合表实体的list
     * @param sysAggPowerDaily
     * @return
     */
    @Override
    public List<SysAggPowerDaily> selectSysAggPowerDailyListBySysAggPowerDaily(SysAggPowerDaily sysAggPowerDaily) {
        QueryWrapper<SysAggPowerDaily> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(sysAggPowerDaily);
        return sysAggPowerDailyMapper.selectList(queryWrapper);
    }

    /**
    * 管理员-首页-平台有效算力排行榜-查询算力按天聚合表里的挖矿效率、算力增速
    *
    * @description
    * @author shangbin
    * @date 2021/6/8 15:30
    * @return com.mei.hui.miner.model.PowerAvailableFilVO
    * @version v1.0.0
    */
    @Override
    public PowerAvailableFilVO selectPowerAvailableByDateAndUserIdList(String yesterDayDate,List<String> minerIdList,String type){
        return sysAggPowerDailyMapper.selectPowerAvailableByDateAndUserIdList(yesterDayDate,minerIdList,type);
    }

    /*查询算力按天聚合表里昨天所有的累计出块份数*/
    @Override
    public Long selectTotalBlocksByDate(String yesterDayDate, String type, String minerId) {
        return sysAggPowerDailyMapper.selectTotalBlocksByDate(yesterDayDate, type, minerId);
    }

    /*查询FIL币算力按天聚合表里昨天所有的有效算力*/
    @Override
    public BigDecimal selectPowerIncreaseByDate(String yesterDayDate, String type, String minerId) {
        return sysAggPowerDailyMapper.selectPowerIncreaseByDate(yesterDayDate, type, minerId);
    }

    /*查询FIL币算力按天聚合表里昨天所有的累计出块奖励*/
    @Override
    public BigDecimal selectTotalBlockAwardByDate(String yesterDayDate, String type, String minerId) {
        return sysAggPowerDailyMapper.selectTotalBlockAwardByDate(yesterDayDate, type, minerId);
    }

    @Override
    public Long totalBlocksByMinerId(String yesterDayDate,List<Long> userIds) {
        LambdaQueryWrapper<SysMinerInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMinerInfo::getUserId,userIds);
        List<SysMinerInfo> miners = minerInfoService.list(wrapper);
        log.info("userIds:{},miners:{}", JSON.toJSONString(userIds),JSON.toJSONString(miners));
        List<String> minerIds = miners.stream().map(v -> v.getMinerId()).collect(Collectors.toList());

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.select("coalesce(sum(total_blocks),0) as total");
        queryWrapper.eq("type",CurrencyEnum.FIL.name());
        queryWrapper.eq("date",yesterDayDate);
        queryWrapper.in("miner_id",minerIds);
        List<Map<String,Object>> list = sysAggPowerDailyMapper.selectMaps(queryWrapper);
        log.info("区块数量:{}",JSON.toJSONString(list));
        return Long.valueOf(String.valueOf(list.get(0).get("total")));
    }
}
