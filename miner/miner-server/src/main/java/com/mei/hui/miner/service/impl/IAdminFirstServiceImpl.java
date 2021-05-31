package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.miner.mapper.ChiaMinerMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.model.AdminFirstCollectVO;
import com.mei.hui.miner.model.PowerAvailableFilVO;
import com.mei.hui.miner.service.IAdminFirstService;
import com.mei.hui.miner.service.IChiaMinerService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class IAdminFirstServiceImpl implements IAdminFirstService {

    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Autowired
    private IChiaMinerService chiaMinerService;

    @Autowired
    private ChiaMinerMapper chiaMinerMapper;

    /**
     * fil管理员首页-旷工统计数据
     * @return
     */
    @Override
    public AdminFirstCollectVO filAdminFirstAllCount() {
        AdminFirstCollectVO adminFirstCollectVO = new AdminFirstCollectVO();
        // 管理员首页-旷工统计数据-平台总资产，用的字段：挖矿账户余额, 单位FIL
        BigDecimal allBalanceMinerAccount = sysMinerInfoService.selectFilAllBalanceMinerAccount();
        adminFirstCollectVO.setAllBalanceMinerAccount(allBalanceMinerAccount);
        // 管理员首页-旷工统计数据-平台有效算力
        BigDecimal allPowerAvailable = sysMinerInfoService.selectFilAllPowerAvailable();
        adminFirstCollectVO.setAllPowerAvailable(allPowerAvailable);
        // 管理员首页-旷工统计数据-活跃旷工
        Long allMinerCount = sysMinerInfoService.selectFilAllMinerIdCount();
        adminFirstCollectVO.setAllMinerCount(allMinerCount);
        // 管理员首页-旷工统计数据-当天出块份数
        Long allBlocksPerDay = sysMinerInfoService.selectFilAllBlocksPerDay();
        adminFirstCollectVO.setAllBlocksPerDay(allBlocksPerDay);
        return adminFirstCollectVO;
    }

    /**
     * chia管理员首页-旷工统计数据
     * @return
     */
    @Override
    public AdminFirstCollectVO chiaAdminFirstAllCount() {
        AdminFirstCollectVO adminFirstCollectVO = new AdminFirstCollectVO();
        // 管理员首页-旷工统计数据-平台总资产，用的字段：挖矿账户余额, 单位FIL
        BigDecimal allBalanceMinerAccount = chiaMinerService.selectFilAllBalanceMinerAccount();
        adminFirstCollectVO.setAllBalanceMinerAccount(allBalanceMinerAccount);
        // 管理员首页-旷工统计数据-平台有效算力
        BigDecimal allPowerAvailable = chiaMinerService.selectFilAllPowerAvailable();
        adminFirstCollectVO.setAllPowerAvailable(allPowerAvailable);
        // 管理员首页-旷工统计数据-活跃旷工
        Long allMinerCount = chiaMinerService.selectFilAllMinerIdCount();
        adminFirstCollectVO.setAllMinerCount(allMinerCount);
        // 管理员首页-旷工统计数据-当天出块份数
        String yesterDayDate = DateUtils.getYesterDayDateYmd();
        Long allBlocksPerDay = chiaMinerService.selectFilAllBlocksPerDay(yesterDayDate);
        adminFirstCollectVO.setAllBlocksPerDay(allBlocksPerDay);
        return adminFirstCollectVO;
    }

    /**
     * fil管理员首页-平台有效算力排行榜
     * @param yesterDayDate
     * @param basePage
     * @return
     */
    @Override
    public Map<String,Object> filPowerAvailablePage(String yesterDayDate,BasePage basePage) {
        // 管理员首页-旷工统计数据-平台有效算力
        BigDecimal allPowerAvailable = sysMinerInfoService.selectFilAllPowerAvailable();
        Page<PowerAvailableFilVO> powerAvailableFilVOPage = new Page<>(basePage.getPageNum(),basePage.getPageSize());
        IPage<PowerAvailableFilVO> result = sysMinerInfoMapper.powerAvailablePage(powerAvailableFilVOPage,yesterDayDate,allPowerAvailable);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",powerAvailableFilVOPage.getRecords());
        map.put("total",powerAvailableFilVOPage.getTotal());
        return map;
    }


    /**
     * chia管理员首页-平台有效算力排行榜
     * @param yesterDayDate
     * @param basePage
     * @return
     */
    @Override
    public Map<String,Object> chiaPowerAvailablePage(String yesterDayDate,BasePage basePage) {
        // 管理员首页-旷工统计数据-平台有效算力
        BigDecimal allPowerAvailable = chiaMinerService.selectFilAllPowerAvailable();
        Page<PowerAvailableFilVO> powerAvailableFilVOPage = new Page<>(basePage.getPageNum(),basePage.getPageSize());
        IPage<PowerAvailableFilVO> result = chiaMinerMapper.powerAvailablePage(powerAvailableFilVOPage,yesterDayDate,allPowerAvailable);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",powerAvailableFilVOPage.getRecords());
        map.put("total",powerAvailableFilVOPage.getTotal());
        return map;
    }
}
