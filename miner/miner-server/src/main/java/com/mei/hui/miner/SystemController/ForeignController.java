package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.service.FilBaselinePowerDayAggService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "对外部系统提供api接口")
@RestController
@RequestMapping("/k2Pool")
public class ForeignController {

    @Autowired
    private FilBaselinePowerDayAggService baselinePowerDayAggService;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    /**
    * 对外API-全网数据
    * @author shangbin
    * @date 2021/7/3 17:45
    * @param []
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.ForeignNetworkVO>
    */
    @ApiOperation(value = "对外API-全网数据")
    @GetMapping("/network")
    public Result<ForeignNetworkVO> selectForeignNetwork(){
        return baselinePowerDayAggService.selectForeignNetwork();
    }

    /**
    * 对外API-平台数据
    *
    * @description
    * @author shangbin
    * @date 2021/7/3 17:57
    * @param []
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.ForeignPlatformVO>
    * @version v1.4.1
    */
    @ApiOperation(value = "对外API-平台数据")
    @GetMapping("/platform")
    public Result<ForeignPlatformVO> selectForeignPlatform(){
        return baselinePowerDayAggService.selectForeignPlatform();
    }

    /**
    * 对外API-矿工数据
    *
    * @description
    * @author shangbin
    * @date 2021/7/5 11:53
    * @param [foreignMinerBO]
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.ForeignSysMinerInfoVO>
    * @version v1.4.1
    */
    @ApiOperation(value = "对外API-矿工数据")
    @PostMapping("/miner")
    public Result<ForeignSysMinerInfoVO> selectForeignMiner(@RequestBody ForeignMinerBO foreignMinerBO){
        return sysMinerInfoService.selectForeignMiner(foreignMinerBO);
    }

    /**
    * 对外API-用户数据
    *
    * @description
    * @author shangbin
    * @date 2021/7/5 11:57
    * @param [foreignUserBO]
    * @return com.mei.hui.util.Result<com.mei.hui.miner.feign.vo.ForeignSysMinerInfoVO>
    * @version v1.4.1
    */
    @ApiOperation(value = "对外API-用户数据")
    @PostMapping("/user")
    public Result<List<ForeignSysMinerInfoVO>> selectForeignUser(@RequestBody ForeignUserBO foreignUserBO){
        return sysMinerInfoService.selectForeignUser(foreignUserBO);
    }

}
