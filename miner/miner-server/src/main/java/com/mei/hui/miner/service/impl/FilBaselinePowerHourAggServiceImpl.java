package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.FilBaselinePowerHourAgg;
import com.mei.hui.miner.entity.FilReportGas;
import com.mei.hui.miner.entity.FilReportNetworkData;
import com.mei.hui.miner.entity.MinerAggData;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBaselinePowerHourAggMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.FilBaselinePowerHourAggService;
import com.mei.hui.miner.service.FilReportGasService;
import com.mei.hui.miner.service.ISysAggPowerHourService;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * filcoin 基线和有效算力聚合表，按天聚合 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-06-23
 */
@Slf4j
@Service
public class FilBaselinePowerHourAggServiceImpl extends ServiceImpl<FilBaselinePowerHourAggMapper, FilBaselinePowerHourAgg>
        implements FilBaselinePowerHourAggService {

    @Autowired
    private FilReportNetworkDataServiceImpl reportNetworkDataService;
    @Autowired
    private SysMinerInfoMapper minerInfoMapper;
    @Autowired
    private FilReportGasService reportGasService;
    @Autowired
    private ISysAggPowerHourService sysAggPowerHourService;
    @Autowired
    private FilBaselinePowerHourAggMapper filBaselinePowerHourAggMapper;

    /**
     * 免登陆首页，全网数据占比计算，扇区封装成本展示
     * @return
     */
    @Override
    public Result<GeneralViewVo> generalView(){
        GeneralViewVo generalViewVo = new GeneralViewVo();
        //全网数据获取:累计出块奖励，全网算力，全网今日出块数，全网活跃矿工
        pushNetWordDataVo(generalViewVo);

        //平台数据:累计出块奖励，算力，今日出块数，活跃矿工
        pushPlatformDataVo(generalViewVo);

        //扇区封装成本
        pushThirtyTwoGasVO(generalViewVo);
        return Result.success(generalViewVo);
    }

    /**
     * 平台数据:累计出块奖励，算力，今日出块数，活跃矿工
     * @param generalViewVo
     */
    public void pushPlatformDataVo(GeneralViewVo generalViewVo){
        /**
         * 平台数据,累计出块奖励，全平台算力
         */
        PlatformDataVo platformData = new PlatformDataVo();
        MinerAggData minerAggData = minerInfoMapper.getMinerAggData();
        log.info("平台数据,累计出块奖励,全平台算力,全平台累计出块数量:{}",JSON.toJSONString(minerAggData));
        if(minerAggData != null){
            platformData.setPower(minerAggData.getTotalPower())
                    .setTotalBlockAward(BigDecimalUtil.formatFour(minerAggData.getTotalBlockAward()));
        }
        /**
         * 活跃矿工
         */
        long activityMinerCount = minerInfoMapper.getActivityMinerCount();
        log.info("平台活跃矿工数量:{}",activityMinerCount);
        platformData.setActiveMiner(activityMinerCount);

        // 计算近24小时出块数量
        // 查询FIL币算力按小时聚合表里近24小时所有的每小时出块份数总和
        String startDate = DateUtils.lDTYesterdayBeforeLocalDateTimeHour();
        String endDate = DateUtils.lDTBeforeBeforeLocalDateTimeHour();
        log.info("入参minerId：【{}】,startDate：【{}】，endDate：【{}】",null,startDate,endDate);
        Long twentyFourTotalBlocks = sysAggPowerHourService.selectTwentyFourTotalBlocks(CurrencyEnum.FIL.name(),null,startDate,endDate);
        log.info("查询FIL币算力按小时聚合表里近24小时所有的每小时出块份数总和出参：【{}】",twentyFourTotalBlocks);
        twentyFourTotalBlocks = twentyFourTotalBlocks == null?0L:twentyFourTotalBlocks;
        log.info("查询FIL币算力按小时聚合表里近24小时所有的每小时出块份数总和出参修改格式后的：【{}】",twentyFourTotalBlocks);
        platformData.setPerDayBlocks(twentyFourTotalBlocks);

        generalViewVo.setPlatformData(platformData);
    }

    /**
     * 32G扇区封装成本
     * @param generalViewVo
     */
    public void pushThirtyTwoGasVO(GeneralViewVo generalViewVo){
        LambdaQueryWrapper<FilReportGas> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(FilReportGas::getCreateTime);
        IPage<FilReportGas> page = reportGasService.page(new Page<>(0, 1), queryWrapper);
        log.info("扇区封装成本:{}",JSON.toJSONString(page.getRecords()));

        if(page != null && page.getRecords().size() > 0){
            FilReportGas data = page.getRecords().get(0);
            //32G扇区封装成本
            ThirtyTwoGasVO thirtyTwoGasVO = new ThirtyTwoGasVO();
            thirtyTwoGasVO.setCost(BigDecimalUtil.formatFour(data.getThirtyTwoCost()))
                    .setGas(formatBigdecimal(data.getThirtyTwoGas()))
                    .setPledge(BigDecimalUtil.formatFour(data.getThirtyTwoPledge()));

            //64G扇区封装成本
            SixtyFourGasVO sixtyFourGasVO = new SixtyFourGasVO();
            sixtyFourGasVO.setCost(BigDecimalUtil.formatFour(data.getSixtyFourCost()))
                    .setGas(formatBigdecimal(data.getSixtyFourGas()))
                    .setPledge(BigDecimalUtil.formatFour(data.getSixtyFourPledge()));

            generalViewVo.setThirtyTwoGasVO(thirtyTwoGasVO)
                    .setSixtyFourGasVO(sixtyFourGasVO);
        }
    }

    /**
     * 全网数据获取:累计出块奖励，全网算力，全网今日出块数，全网活跃矿工
     * @param generalViewVo
     */
    public void pushNetWordDataVo(GeneralViewVo generalViewVo){
        NetWordDataVo netWordData = new NetWordDataVo();
        //全网今日出块
        Long perDayBlocks = 0L;
        /**
         * 全网数据:累计出块奖励,全网算力,全网出块份数,全网活跃矿工
         */
        List<FilReportNetworkData> networkDatas = reportNetworkDataService.list();
        log.info("计出块奖励,全网算力,全网出块份数,全网活跃矿工:{}", JSON.toJSONString(networkDatas));

        if(networkDatas.size() > 0){
            FilReportNetworkData filReportNetworkData = networkDatas.get(0);
            netWordData.setActiveMiner(filReportNetworkData.getActiveMiner())
                    .setPower(filReportNetworkData.getPower())
                    .setTotalBlockAward(BigDecimalUtil.formatFour(filReportNetworkData.getTotalBlockAward()));
            perDayBlocks = filReportNetworkData.getBlocks();
            generalViewVo.setBlockHeight(filReportNetworkData.getBlockHeight());
        }

        // 近24小时出块数量
        // LocalDateTime获取昨天的上上一个时间点,如现在为2021-08-30 15:30:00,上上一个整点时间点为，2021-08-29 14:00:00
        String startDate = DateUtils.lDTYesterdayBeforeBeforeLocalDateTimeHourDate();
        // LocalDateTime获取今天的上上一个时间点,如现在为2021-08-30 15:30:00,上上一个整点时间点为，2021-08-30 14:00:00
        String endDate = DateUtils.lDTBeforeBeforeLocalDateTimeHour();
        log.info("startDate：【{}】,endDate：【{}】",startDate,endDate);
        // 获取昨天的 全网累计出块份数
        Long startBlocks = selectFilBaselinePowerHourAggBlocksByType("start",startDate,endDate);
        startBlocks = startBlocks == null?0L:startBlocks;
        log.info("获取昨天的 全网累计出块份数：【{}】",startBlocks);
        // 获取今天的 全网累计出块份数
        Long endBlocks = selectFilBaselinePowerHourAggBlocksByType("end",startDate,endDate);
        endBlocks = endBlocks == null?0L:endBlocks;
        log.info("获取今天的 全网累计出块份数：【{}】",endBlocks);

        netWordData.setPerDayBlocks(endBlocks - startBlocks);
        generalViewVo.setNetWordData(netWordData);
    }

    /**
     * 根据类型、开始时间、结束时间查询  filcoin 基线和有效算力聚合表，按小时聚合表 的 全网累计出块份数
     * @param date
     * @return
     */
    public Long selectFilBaselinePowerHourAggBlocksByType(String type,String startDate,String endDate){
        return  filBaselinePowerHourAggMapper.selectFilBaselinePowerHourAggBlocksByType(type, startDate, endDate);
    }

    /**
     *从左向右搜取两个数字进行显示，例如：0.0000003498031908 显示 0.00000034
     * @param val
     * @return
     */
    public static BigDecimal formatBigdecimal(BigDecimal val){
        if(val == null){
            return null;
        }
        //如果大于0，直接返回

        if(val.compareTo(new BigDecimal("1")) > 1){
            return val.setScale(2, BigDecimal.ROUND_DOWN);
        }
        char[] array = val.toPlainString().toCharArray();
        int pointIndex = 0;
        int firstNotZeroIndex = 0;
        for(int i=0;i<array.length;i++){
            if(".".equals(array[i]+"")){
                pointIndex=i;
            }
            if(!".".equals(array[i]+"") && !"0".equals(array[i]+"")){
                firstNotZeroIndex = i;
                break;
            }
        }
        return val.setScale(firstNotZeroIndex - pointIndex + 1, BigDecimal.ROUND_DOWN);
    }


}
