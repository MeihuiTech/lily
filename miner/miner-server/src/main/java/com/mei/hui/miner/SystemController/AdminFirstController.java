package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.model.AdminFirstCollectVO;
import com.mei.hui.miner.service.IAdminFirstService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@Api(tags = "管理员-首页")
@RestController
@RequestMapping("/admin/first")
public class AdminFirstController {
    @Autowired
    private IAdminFirstService adminFirstService;

    @ApiOperation(value = "管理员首页-矿工统计数据",notes = "管理员首页-矿工统计数据出参：\n" +
            "\n" +
            "allBalanceMinerAccount平台总资产\n" +
            "allPowerAvailable平台有效算力\n" +
            "allMinerCount活跃矿工\n" +
            "allBlocksPerDay当天出块份数")
    @GetMapping("/allCount")
    public Result adminFirstAllCount(){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        AdminFirstCollectVO adminFirstCollectVO = new AdminFirstCollectVO();
        if(CurrencyEnum.FIL.getCurrencyId().equals(currencyId)){//fil 币
            adminFirstCollectVO = adminFirstService.filAdminFirstAllCount();
            return Result.success(adminFirstCollectVO);
        }else if(CurrencyEnum.XCH.getCurrencyId().equals(currencyId)){//起亚币
            adminFirstCollectVO = adminFirstService.chiaAdminFirstAllCount();
            return Result.success(adminFirstCollectVO);
        }
        return Result.OK;
    }

   /* @ApiOperation(value = "管理员首页-平台有效算力排行榜",notes = "管理员首页-平台有效算力排行榜出参：\n" +
            "\n" +
            "userId用户ID\n" +
            "powerAvailable有效算力, 单位B\n" +
            "powerAvailablePercent有效算力所占百分比\n" +
            "totalBlockAward累计出块奖励,单位FIL\n" +
            "miningEfficiency挖矿效率\n" +
            "powerIncrease算力增速\n"+
            "totalBlocksPerDay今日出块份数\n"+
            "totalSectorAvailable有效扇区\n"+
            "totalSectorError错误扇区\n")
    @GetMapping("/powerAvailablePage")
     public Map<String,Object> powerAvailablePage(BasePage basePage){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        String yesterDayDate = DateUtils.getYesterDayDateYmd();
        if(CurrencyEnum.FIL.getCurrencyId().equals(currencyId)){//fil 币
            return adminFirstService.filPowerAvailablePage(yesterDayDate,basePage);
        }else if(CurrencyEnum.XCH.getCurrencyId().equals(currencyId)){//起亚币
            return adminFirstService.chiaPowerAvailablePage(yesterDayDate,basePage);
        }
        return null;
    }
*/



}
