package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.GetAssetRateBO;
import com.mei.hui.miner.feign.vo.GetMonyRateVO;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/asset")
@Api(value="资产统计类", tags = "资产统计类")
public class AssetController {
    @Autowired
    private ISysAggAccountDailyService iSysAggAccountDailyService;
    /**
     * 计算每个币种的资产占比
     * @param getAssetRateBO
     * @return
     */
    @ApiOperation("用户资产占比【鲍红建】")
    @PostMapping("/getAssetRate")
    public Result<List<GetMonyRateVO>> getAssetRate(@RequestBody GetAssetRateBO getAssetRateBO){
        return iSysAggAccountDailyService.getAssetRate(getAssetRateBO);
    }
}
