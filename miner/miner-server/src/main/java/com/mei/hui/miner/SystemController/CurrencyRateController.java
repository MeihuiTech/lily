package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.model.CurrencyRateBO;
import com.mei.hui.miner.model.SaveFeeRateBO;
import com.mei.hui.miner.service.CurrencyRateService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Api(tags = "费率相关")
@RequestMapping("/rate")
public class CurrencyRateController {

    @Autowired
    private CurrencyRateService currencyRateService;

    @ApiOperation("保存币种费率，支持新增、更新【鲍红建】")
    @PostMapping("/saveOrUpdateFeeRate")
    public Result saveOrUpdateFeeRate(@RequestBody SaveFeeRateBO saveFeeRateBO){
        /**
         * 校验
         */
        if(saveFeeRateBO.getUserId() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"userId 不能为空");
        }
        List<CurrencyRateBO> rats = saveFeeRateBO.getRats();
        if(rats.size() == 0){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"币种费率不能为空");
        }
        List<String> list = new ArrayList<>();
        rats.stream().forEach(v->{
            if(CurrencyEnum.getCurrencyUnitByType(v.getType()) == null){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"币种"+v.getType()+"不存在");
            }
            if(list.contains(v.getType())){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"币种"+v.getType()+"重复");
            }
            list.add(v.getType());
        });
        return currencyRateService.saveFeeRate(saveFeeRateBO);
    }


}
