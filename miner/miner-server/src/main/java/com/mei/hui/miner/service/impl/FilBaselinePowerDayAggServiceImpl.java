package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBaselinePowerDayAggMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.FilBaselinePowerDayAggService;
import com.mei.hui.miner.service.FilReportGasService;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class FilBaselinePowerDayAggServiceImpl extends ServiceImpl<FilBaselinePowerDayAggMapper, FilBaselinePowerDayAgg>
        implements FilBaselinePowerDayAggService {

    @Autowired
    private FilReportNetworkDataServiceImpl reportNetworkDataService;
    @Autowired
    private SysMinerInfoMapper minerInfoMapper;
    @Autowired
    private FilReportGasService reportGasService;

    /**
     * 免登陆首页，全网数据占比计算，扇区封装成本展示
     * @return
     */
    public Result<GeneralViewVo> generalView(){
        GeneralViewVo generalViewVo = new GeneralViewVo();
        //全网数据获取:累计出块奖励，全网算力，全网今日出块数，全网活跃旷工
        pushNetWordDataVo(generalViewVo);

        //平台数据:累计出块奖励，算力，今日出块数，活跃旷工
        pushPlatformDataVo(generalViewVo);

        //扇区封装成本
        pushThirtyTwoGasVO(generalViewVo);
        return Result.success(generalViewVo);
    }

    /**
     * 平台数据:累计出块奖励，算力，今日出块数，活跃旷工
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
            platformData.setPower(minerAggData.getTotalPower()).setTotalBlockAward(BigDecimalUtil.formatFour(minerAggData.getTotalBlockAward()));
        }
        /**
         * 活跃旷工
         */
        long activityMinerCount = minerInfoMapper.getActivityMinerCount();
        log.info("平台活跃矿工数量:{}",activityMinerCount);
        platformData.setActiveMiner(activityMinerCount);
        /**
         * 计算今日出块数量
         */
        long yesterdayTotalBlocksCount = minerInfoMapper.getYesterdayTotalBlocksCount(LocalDate.now().minusDays(1).toString());
        log.info("全平台昨日出块总数:{}",yesterdayTotalBlocksCount);
        platformData.setPerDayBlocks(minerAggData.getTotalBlocks()-yesterdayTotalBlocksCount);

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
            thirtyTwoGasVO.setCost(data.getThirtyTwoCost()).setGas(data.getThirtyTwoGas()).setPledge(data.getThirtyTwoPledge());

            //64G扇区封装成本
            SixtyFourGasVO sixtyFourGasVO = new SixtyFourGasVO();
            sixtyFourGasVO.setCost(data.getSixtyFourCost()).setGas(data.getSixtyFourGas()).setPledge(data.getSixtyFourPledge());

            generalViewVo.setThirtyTwoGasVO(thirtyTwoGasVO).setSixtyFourGasVO(sixtyFourGasVO);
        }
    }

    /**
     * 全网数据获取:累计出块奖励，全网算力，全网今日出块数，全网活跃旷工
     * @param generalViewVo
     */
    public void pushNetWordDataVo(GeneralViewVo generalViewVo){
        NetWordDataVo netWordData = new NetWordDataVo();
        //全网今日出块
        Long perDayBlocks = 0L;
        /**
         * 全网数据:累计出块奖励,全网算力,全网出块份数,全网活跃旷工
         */
        List<FilReportNetworkData> networkDatas = reportNetworkDataService.list();
        log.info("计出块奖励,全网算力,全网出块份数,全网活跃旷工:{}", JSON.toJSONString(networkDatas));

        if(networkDatas.size() > 0){
            FilReportNetworkData filReportNetworkData = networkDatas.get(0);
            netWordData.setActiveMiner(filReportNetworkData.getActiveMiner())
                    .setPower(filReportNetworkData.getPower())
                    .setTotalBlockAward(filReportNetworkData.getTotalBlockAward());
            perDayBlocks = filReportNetworkData.getBlocks();
            generalViewVo.setBlockHeigh(filReportNetworkData.getBlockHeight());
        }

        /**
         * 获取昨天全网累计出块数
         */
        LambdaQueryWrapper<FilBaselinePowerDayAgg> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FilBaselinePowerDayAgg::getDate, LocalDate.now().minusDays(1));
        List<FilBaselinePowerDayAgg> yesterdayPowers = this.list(queryWrapper);
        log.info("昨日全网数据聚合信息:{}",JSON.toJSONString(yesterdayPowers));

        if(yesterdayPowers.size() > 0){
            FilBaselinePowerDayAgg yesterdayData = yesterdayPowers.get(0);
            perDayBlocks = perDayBlocks - yesterdayData.getBlocks();
        }
        netWordData.setPerDayBlocks(perDayBlocks);
        generalViewVo.setNetWordData(netWordData);
    }

    /**
     * 全网基线算力走势图
     * @return
     */
    public Result<List<BaselineAndPowerVO>> baselineAndPower(){
        LambdaQueryWrapper<FilBaselinePowerDayAgg> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.gt(FilBaselinePowerDayAgg::getDate,LocalDate.now().minusDays(30));
        queryWrapper.orderByAsc(FilBaselinePowerDayAgg ::getDate);
        List<FilBaselinePowerDayAgg> list = this.list(queryWrapper);
        log.info("获取近30的基线算力数据:{}",JSON.toJSONString(list));

        List<BaselineAndPowerVO> lt = list.stream().map(v -> {
            BaselineAndPowerVO baselineAndPower = new BaselineAndPowerVO();
            BeanUtils.copyProperties(v, baselineAndPower);
            return baselineAndPower;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }

    /**
     * Gas消耗走势图
     * @return
     */
    public Result<List<GaslineVO>> gasline(){
        LambdaQueryWrapper<FilReportGas> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.gt(FilReportGas::getDate, LocalDateTime.now().minusHours(3));
        List<FilReportGas> list = reportGasService.list(queryWrapper);
        log.info("近3小时的gas消耗数据:{}",list.size());
        List<GaslineVO> lt = list.stream().map(v -> {
            GaslineVO gaslineVO = new GaslineVO();
            gaslineVO.setDate(v.getDate());
            gaslineVO.setThirtyTwoGas(BigDecimalUtil.formatFour(v.getThirtyTwoGas()));
            gaslineVO.setSixtyFourGas(BigDecimalUtil.formatFour(v.getSixtyFourGas()));
            return gaslineVO;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }

}
