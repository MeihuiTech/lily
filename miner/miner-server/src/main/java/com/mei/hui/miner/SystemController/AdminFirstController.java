package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.feign.vo.QiniuVO;
import com.mei.hui.miner.model.AdminFirstCollectVO;
import com.mei.hui.miner.service.GeneralService;
import com.mei.hui.miner.service.IAdminFirstService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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

   @ApiOperation("管理员获取七牛磁盘容量")
   @PostMapping("/findDiskSize")
   public Result<List<QiniuVO>> findDiskSize(){
       return adminFirstService.findDiskSize();
   }



}
