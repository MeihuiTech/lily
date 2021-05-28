package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.model.SysCurrencyVO;
import com.mei.hui.miner.service.AdminFirstService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Api(tags = "管理员-首页")
@RestController
@RequestMapping("/admin")
public class AdminFirstApi {
    @Autowired
    private AdminFirstService adminFirstService;

    @ApiOperation("管理员首页-旷工统计数据")
    @GetMapping("/first")
    public Result first(){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){//fil 币
            return null;
        }else if(CurrencyEnum.CHIA.getCurrencyId() == currencyId){//起亚币
            return null;
        }
        return null;
    }


}
