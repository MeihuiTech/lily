package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.service.FilBaselinePowerDayAggService;
import com.mei.hui.miner.service.FilBaselinePowerHourAggService;
import com.mei.hui.miner.service.GeneralService;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "免登陆首页")
@RestController
@RequestMapping("/noAuthority")
public class GeneralController {
    @Autowired
    private UserManager userManager;
    @Autowired
    private GeneralService generalService;

    @Autowired
    private FilBaselinePowerDayAggService baselinePowerDayAggService;
    @Autowired
    private FilBaselinePowerHourAggService baselinePowerHourAggService;
    /**
     * 新增矿工信息
     */
    @ApiOperation(value = "免登陆首页，概览-全网与平台指标【鲍红建】")
    @PostMapping("/generalView")
    public Result<GeneralViewVo> generalView(){
        return baselinePowerHourAggService.generalView();
    }

    @ApiOperation(value = "全网:基线算力走势图【鲍红建】")
    @PostMapping("/baselineAndPower")
    public Result<List<BaselineAndPowerVO>> baselineAndPower(){
        return baselinePowerDayAggService.baselineAndPower();
    }

    @ApiOperation(value = "全网:近3小时封装Gas费用走势图【鲍红建】")
    @PostMapping("/gasline")
    public Result<List<GaslineVO>> gasline(){
        return baselinePowerDayAggService.gasline();
    }

    @RequestMapping(value = "/findAllUser",method = RequestMethod.POST)
    public List<SysUserOut> findAllUser(){
        return userManager.findAllUser();
    }

    @ApiOperation("大屏磁盘容量")
    @PostMapping("/findDiskSizeInfo")
    public Result<List<FindDiskSizeInfoBO>> findDiskSizeInfo(){
        return generalService.findDiskSizeInfo();
    }

    @ApiOperation("大屏，获取集群宽带")
    @PostMapping("/findClusterBroadband")
    public Result<List<ClusterBroadbandBO>> findClusterBroadband(){
        return generalService.findClusterBroadband();
    }

    @ApiOperation("大屏，有效算力")
    @PostMapping("/availablePower")
    public Result<List<AvailablePowerVO>> availablePower(){
        return generalService.availablePower();
    }

    @ApiOperation("大屏-基础数据接口：累计出块、今天出块、账户资产、总算力、在线设备、活跃存储")
    @PostMapping("/platformBaseInfo")
    public Result<PlatformBaseInfoVO> platformBaseInfo(){
        return generalService.platformBaseInfo();
    }

    @ApiOperation("大屏-账户余额")
    @PostMapping("/accountInfo")
    public Result<List<AccountInfoVO>> accountInfo(){
        return generalService.accountInfo();
    }
}
