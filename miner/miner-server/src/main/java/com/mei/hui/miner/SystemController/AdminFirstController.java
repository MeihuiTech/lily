package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.model.AdminFirstCollectFilVO;
import com.mei.hui.miner.model.PowerAvailableFilVO;
import com.mei.hui.miner.model.SysCurrencyVO;
import com.mei.hui.miner.service.AdminFirstService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;


@Slf4j
@Api(tags = "管理员-首页")
@RestController
@RequestMapping("/admin/first")
public class AdminFirstController {
    @Autowired
    private AdminFirstService adminFirstService;

    @ApiOperation("管理员首页-旷工统计数据")
    @GetMapping("/allCount")
    public Result adminFirstAllCount(){
        // TODO 自己测试使用，一会修改回来
//        Long currencyId = HttpRequestUtil.getCurrencyId();
        Long currencyId = 1L;
        if(CurrencyEnum.FIL.getCurrencyId().equals(currencyId)){//fil 币
            AdminFirstCollectFilVO adminFirstCollectFilVO = new AdminFirstCollectFilVO();
            // 管理员首页-旷工统计数据-平台总资产，用的字段：挖矿账户余额, 单位FIL
            BigDecimal allBalanceMinerAccount = adminFirstService.selectAllBalanceMinerAccount();
            adminFirstCollectFilVO.setAllBalanceMinerAccount(allBalanceMinerAccount);
            // 管理员首页-旷工统计数据-平台有效算力
            BigDecimal allPowerAvailable = adminFirstService.selectAllPowerAvailable();
            adminFirstCollectFilVO.setAllPowerAvailable(allPowerAvailable);
            // 管理员首页-旷工统计数据-活跃旷工
            Long allMinerCount = adminFirstService.selectAllMinerIdCount();
            adminFirstCollectFilVO.setAllMinerCount(allMinerCount);
            // 管理员首页-旷工统计数据-当天出块份数
            Long allBlocksPerDay = adminFirstService.selectAllBlocksPerDay();
            adminFirstCollectFilVO.setAllBlocksPerDay(allBlocksPerDay);
            return Result.success(adminFirstCollectFilVO);
        }else if(CurrencyEnum.CHIA.getCurrencyId().equals(currencyId)){//起亚币
            return null;
        }
        return null;
    }


    @ApiOperation("管理员首页-平台有效算力排行榜")
    @GetMapping("/powerAvailablePage")
     public Map<String,Object> powerAvailablePage(BasePage basePage){
        String yesterDayDate = DateUtils.getYesterDayDateYmd();
        return adminFirstService.powerAvailablePage(yesterDayDate,basePage);
    }

}
