package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.feign.vo.UserMinerBO;
import com.mei.hui.miner.mapper.ChiaMinerMapper;
import com.mei.hui.miner.model.ChiaMinerVO;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.service.CurrencyRateService;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChiaMinerServiceImpl implements IChiaMinerService {
    @Autowired
    private ChiaMinerMapper chiaMinerMapper;
    @Autowired
    private CurrencyRateService currencyRateService;
    @Autowired
    private ISysAggPowerDailyService sysAggPowerDailyService;

    /**
     * 获取 起亚币 旷工列表
     * @param sysMinerInfoBO
     * @return
     */
    @Override
    public Map<String,Object> findChiaMinerPage(SysMinerInfoBO sysMinerInfoBO)
    {
        LambdaQueryWrapper<ChiaMiner> query = new LambdaQueryWrapper();
        query.eq(ChiaMiner::getUserId, HttpRequestUtil.getUserId());
//        query.eq(ChiaMiner::getMinerId,sysMinerInfoBO.getMinerId());
        if("powerAvailable".equals(sysMinerInfoBO.getCloumName())){
            if(sysMinerInfoBO.isAsc()){
                query.orderByAsc(ChiaMiner::getPowerAvailable);
            }else{
                query.orderByDesc(ChiaMiner::getPowerAvailable);
            }
        }else if("balanceMinerAccount".equals(sysMinerInfoBO.getCloumName())){
            if(sysMinerInfoBO.isAsc()){
                query.orderByAsc(ChiaMiner::getBalanceMinerAccount);
            }else{
                query.orderByDesc(ChiaMiner::getBalanceMinerAccount);
            }
        }else if("totalBlockAward".equals(sysMinerInfoBO.getCloumName())){
            if(sysMinerInfoBO.isAsc()){
                query.orderByAsc(ChiaMiner::getTotalBlockAward);
            }else{
                query.orderByDesc(ChiaMiner::getTotalBlockAward);
            }
        }
        IPage<ChiaMiner> page = chiaMinerMapper.selectPage(new Page<>(sysMinerInfoBO.getPageNum(), sysMinerInfoBO.getPageSize()), query);
        List<ChiaMiner> chiaMinerList = page.getRecords();
        List<ChiaMinerVO> chiaMinerVOList = new ArrayList<>();
        if (chiaMinerList != null && chiaMinerList.size() > 0) {
            for (ChiaMiner chiaMiner: chiaMinerList) {
                ChiaMinerVO chiaMinerVO = new ChiaMinerVO();
                BeanUtils.copyProperties(chiaMiner,chiaMinerVO);
                chiaMinerVO.setTotalBlockAward(BigDecimalUtil.formatFour(chiaMinerVO.getTotalBlockAward()));
                chiaMinerVO.setBalanceMinerAccount(BigDecimalUtil.formatFour(chiaMinerVO.getBalanceMinerAccount()));

                SysAggPowerDaily sysAggPowerDaily = new SysAggPowerDaily();
                sysAggPowerDaily.setMinerId(chiaMiner.getMinerId());
                sysAggPowerDaily.setDate(DateUtils.getYesterDayDateYmd());
                sysAggPowerDaily.setType(CurrencyEnum.XCH.name());
                List<SysAggPowerDaily> sysAggPowerDailyList = sysAggPowerDailyService.selectSysAggPowerDailyListBySysAggPowerDaily(sysAggPowerDaily);
                if (sysAggPowerDailyList != null && sysAggPowerDailyList.size() > 0) {
                    chiaMinerVO.setPowerIncreasePerDay(chiaMiner.getPowerAvailable().subtract(sysAggPowerDailyList.get(0).getPowerAvailable()));
                    chiaMinerVO.setBlocksPerDay(chiaMiner.getTotalBlocks() - sysAggPowerDailyList.get(0).getTotalBlocks());
                }
                chiaMinerVOList.add(chiaMinerVO);
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",chiaMinerVOList);
        map.put("total",page.getTotal());
        return map;
    }

    /**
     * chia管理员首页-旷工统计数据-平台总资产
     * @return
     */
    @Override
    public BigDecimal selectFilAllBalanceMinerAccount() {
        return chiaMinerMapper.selectAllBalanceMinerAccount();
    }

    /**
     * chia管理员首页-旷工统计数据-平台有效算力
     * @return
     */
    @Override
    public BigDecimal selectFilAllPowerAvailable() {
        return chiaMinerMapper.selectAllPowerAvailable();
    }

    /**
     * chia管理员首页-旷工统计数据-活跃旷工
     * @return
     */
    @Override
    public Long selectFilAllMinerIdCount() {
        return chiaMinerMapper.selectAllMinerIdCount();
    }

    /**
     * chia管理员首页-旷工统计数据-当天出块份数
     * @return
     */
    @Override
    public Long selectFilAllBlocksPerDay(String yesterDayDate) {
        return chiaMinerMapper.selectAllBlocksPerDay(yesterDayDate);
    }

    /**
     * 根据用户id、旷工id查询起亚币旷工信息表中是否有数据
     * @param userId
     * @param minerId
     * @return
     */
    @Override
    public List<ChiaMiner> selectChiaMinerByUserIdAndMinerId(Long userId, String minerId) {
        ChiaMiner chiaMiner = new ChiaMiner();
        chiaMiner.setUserId(userId);
        chiaMiner.setMinerId(minerId);
        QueryWrapper<ChiaMiner> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(chiaMiner);
        List<ChiaMiner> chiaMinerList = chiaMinerMapper.selectList(queryWrapper);
        return chiaMinerList;
    }

    @Override
    public int insertChiaMiner(ChiaMiner chiaMiner) {
        return chiaMinerMapper.insert(chiaMiner);
    }

    @Override
    public int updateChiaMiner(ChiaMiner chiaMiner) {
        return chiaMinerMapper.updateById(chiaMiner);
    }

    @Override
    public List<ChiaMiner> findChiaMinerList(ChiaMiner chiaMiner) {
        QueryWrapper<ChiaMiner> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(chiaMiner);
        return chiaMinerMapper.selectList(queryWrapper);
    }

    /**
     * 通过userid集合批量获取旷工总算力、总收益、费率
     * @param userIds
     * @return
     */
    @Override
    public Result<List<AggMinerVO>> findBatchChiaMinerByUserId(UserMinerBO userMinerBO) {
        List<Long> userIds = userMinerBO.getUserIds();
        if(userIds == null || userIds.size() == 0){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"用户集合不能为空");
        }
        List<AggMinerVO> list = chiaMinerMapper.findBatchChiaMinerByUserId(userMinerBO);
        Map<Long,BigDecimal> rateMap = currencyRateService.getUserIdRateMapByUserIdList(userIds,"XCH");
        list.stream().forEach(v -> {
            v.setFeeRate(rateMap.get(v.getUserId()));
        });
        return Result.success(list);
    }

    /**
    * 根据userId查询起亚币旷工信息表里的该用户所有的矿工ID
    *
    * @description
    * @author shangbin
    * @date 2021/6/8 15:05
    * @param [userId]
    * @return java.util.List<java.lang.String>
    * @version v1.0.0
    */
    @Override
    public List<String> findMinerIdByUserId(Long userId){
        ChiaMiner chiaMiner = new ChiaMiner();
        chiaMiner.setUserId(userId);
        QueryWrapper<ChiaMiner> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(chiaMiner);
        List<ChiaMiner> chiaMinerList = chiaMinerMapper.selectList(queryWrapper);
        List<String> minerIdList = chiaMinerList.stream().map(v -> {
            return v.getMinerId();
        }).collect(Collectors.toList());
        return minerIdList;
    }



}
