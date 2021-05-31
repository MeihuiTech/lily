package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.model.AdminFirstCollectVO;
import com.mei.hui.miner.service.IAdminFirstService;
import com.mei.hui.util.BasePage;
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

    @ApiOperation("管理员首页-旷工统计数据")
    @GetMapping("/allCount")
    public Result adminFirstAllCount(){
        // TODO 自己测试使用，一会修改回来
        Long currencyId = HttpRequestUtil.getCurrencyId();
//        Long currencyId = 2L;
        AdminFirstCollectVO adminFirstCollectVO = new AdminFirstCollectVO();
        if(CurrencyEnum.FIL.getCurrencyId().equals(currencyId)){//fil 币
            adminFirstCollectVO = adminFirstService.filAdminFirstAllCount();
            return Result.success(adminFirstCollectVO);
        }else if(CurrencyEnum.CHIA.getCurrencyId().equals(currencyId)){//起亚币
            adminFirstCollectVO = adminFirstService.chiaAdminFirstAllCount();
            return Result.success(adminFirstCollectVO);
        }
        return Result.OK;
    }


    @ApiOperation("管理员首页-平台有效算力排行榜")
    @GetMapping("/powerAvailablePage")
     public Map<String,Object> powerAvailablePage(BasePage basePage){
        // TODO 自己测试使用，一会修改回来
        Long currencyId = HttpRequestUtil.getCurrencyId();
//        Long currencyId = 2L;
        String yesterDayDate = DateUtils.getYesterDayDateYmd();
        if(CurrencyEnum.FIL.getCurrencyId().equals(currencyId)){//fil 币
            return adminFirstService.filPowerAvailablePage(yesterDayDate,basePage);
        }else if(CurrencyEnum.CHIA.getCurrencyId().equals(currencyId)){//起亚币
            return adminFirstService.chiaPowerAvailablePage(yesterDayDate,basePage);
        }
        return null;
    }




}
