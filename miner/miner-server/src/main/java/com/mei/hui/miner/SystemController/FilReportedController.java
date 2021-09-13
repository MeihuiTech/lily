package com.mei.hui.miner.SystemController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.feign.vo.MinerIpLongitudeLatitudeBO;
import com.mei.hui.miner.feign.vo.ReportDeadlinesBO;
import com.mei.hui.miner.feign.vo.ReportGasBO;
import com.mei.hui.miner.feign.vo.ReportNetworkDataBO;
import com.mei.hui.miner.model.RequestMachineInfo;
import com.mei.hui.miner.model.RequestMinerInfo;
import com.mei.hui.miner.model.RequestSectorInfo;
import com.mei.hui.miner.service.*;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 矿工信息Controller
 * 
 * @author ruoyi
 * @date 2021-05-26
 */
@Api(value="fil客户端上报信息", tags = "fil客户端上报信息")
@RestController
@RequestMapping("/fil/reported")
public class FilReportedController {
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private ISysMachineInfoService sysMachineInfoService;
    @Autowired
    private ISysSectorsWrapService sysSectorsWrapService;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private FilReportGasService reportGasService;
    @Autowired
    private FilReportNetworkDataService reportNetworkDataService;
    @Autowired
    private FilDeadlinesService deadlinesService;
    @Autowired
    private IMinerLongitudeLatitudeService minerLongitudeLatitudeService;
    @Autowired
    private NoPlatformMinerService noPlatformMinerService;

    /**
     * 新增矿工信息
     */
    @ApiOperation(value = "新增矿工")
    @PostMapping("/miner")
    public Result miner(@RequestBody RequestMinerInfo sysMinerInfo)
    {
        sysMinerInfo.setSectorAvailable(sysMinerInfo.getSectorActive());
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String apiKey = httpServletRequest.getHeader(SystemConstants.APIKEY);
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"apiKey不存在");
        }
        Long userId = userIdResult.getData();
        sysMinerInfo.setUserId(userId);

        SysUserOut sysUserInput = new SysUserOut();
        sysUserInput.setUserId(userId);
        Result<SysUserOut> userResult = userFeignClient.getUserById(sysUserInput);
        if (!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())
                || userResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"获取用户失败");
        }
        int rows = sysMinerInfoService.insertReportedSysMinerInfo(userId, sysMinerInfo);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }


    /**
     * 新增矿机信息
     */
    @ApiOperation(value = "新增矿机")
    @PostMapping("/machine")
    public Result machine(@RequestBody RequestMachineInfo sysMachineInfo)
    {
        sysMachineInfo.setOnline(1);
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String apiKey = httpServletRequest.getHeader(SystemConstants.APIKEY);
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"apiKey不存在");
        }

        SysMachineInfo machine = sysMachineInfoService.selectSysMachineInfoByMinerAndHostname(sysMachineInfo.getMinerId(),sysMachineInfo.getHostname());
        if (machine == null) {
            int rows = sysMachineInfoService.insertSysMachineInfo(sysMachineInfo);
            return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
        } else {
            sysMachineInfo.setId(machine.getId());
            int rows = sysMachineInfoService.updateSysMachineInfo(sysMachineInfo);
            return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
        }

    }

    /**
     * 批量新增矿机信息
     */
    @ApiOperation(value = "批量新增矿机")
    @PostMapping("/machineList")
    public Result machineList(@RequestBody List<RequestMachineInfo> requestMachineInfoList)
    {
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String apiKey = httpServletRequest.getHeader(SystemConstants.APIKEY);
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"apiKey不存在");
        }

        if (requestMachineInfoList == null || requestMachineInfoList.size() < 1) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"数据为空");
        }
        int rows = sysMachineInfoService.insertSysMachineInfoList(requestMachineInfoList);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
     * 新增扇区信息
     */
    @ApiOperation(value = "新增扇区")
    @PostMapping("/sector")
    public Result sector(@RequestBody RequestSectorInfo sysSectorInfo)
    {
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String apiKey = httpServletRequest.getHeader(SystemConstants.APIKEY);
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"apiKey不存在");
        }

        if (sysSectorInfo == null || StringUtils.isEmpty(sysSectorInfo.getMinerId()) || sysSectorInfo.getSectorNo() == null ){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"入参必填项为空");
        }

        int rows = sysSectorsWrapService.addSector(sysSectorInfo);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    @ApiOperation(value = "Gas费用上报接口")
    @PostMapping("/reportGas")
    public Result reportGas(@RequestBody ReportGasBO bo){
        if(bo.getThirtyTwoGas() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"32G矿工Gas费用,不能为空");
        }
        if(bo.getThirtyTwoCost() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"32G矿工总成本,不能为空");
        }
        if(bo.getThirtyTwoPledge() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"32G矿工质押费用,不能为空");
        }
        if(bo.getSixtyFourGas() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"64G矿工Gas费用,不能为空");
        }
        if(bo.getSixtyFourCost() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"64G矿工总成本,不能为空");
        }
        if(bo.getSixtyFourPledge() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"64G矿工质押费用,不能为空");
        }
        if(bo.getSixtyFourGas().doubleValue() == 0 || bo.getThirtyTwoGas().doubleValue() == 0){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"Gas费用,不能为零");
        }
        return reportGasService.reportGas(bo);
    }

    @ApiOperation(value = "全网数据上报:累计出块奖励、有效算力、累计出块份数、全网活跃矿工、全网区块高度")
    @PostMapping("/reportNetworkData")
    public Result reportNetworkData(@RequestBody ReportNetworkDataBO bo){
        if(bo.getActiveMiner() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"全网活跃矿工,不能为空");
        }
        if(bo.getBlockHeight() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"全网区块高度,不能为空");
        }
        if(bo.getBlocks() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"全网累计出块份数,不能为空");
        }
        if(bo.getPower() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"全网有效算力,不能为空");
        }
        if(bo.getTotalBlockAward() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"全网累计出块奖励,不能为空");
        }
        return reportNetworkDataService.reportNetworkData(bo);
    }

    @ApiOperation(value = "窗口上报")
    @PostMapping("/reportDeadlines")
    public Result reportDeadlines(@RequestBody ReportDeadlinesBO bo){
        if(bo.getWindows().size() != 48){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"请传48个窗口,不能为空");
        }
        if(bo.getMinerId() == null){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"minerId不能为空");
        }
        return deadlinesService.reportDeadlines(bo);
    }


    @ApiOperation(value = "上报矿工ip")
    @PostMapping("/reportMinerIp")
    public Result reportMinerIpLongitudeLatitude(@RequestBody MinerIpLongitudeLatitudeBO minerIpLongitudeLatitudeBO){
        if (StringUtils.isEmpty(minerIpLongitudeLatitudeBO.getMinerId())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"minerId为空");
        }
        if (StringUtils.isEmpty(minerIpLongitudeLatitudeBO.getIp())){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"ip为空");
        }
        Integer rows = minerLongitudeLatitudeService.reportMinerIpLongitudeLatitude(minerIpLongitudeLatitudeBO);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    @ApiOperation(value = "初始化矿工ip,地理位置")
    @PostMapping("/initMinerIp")
    public Result initMinerIp(){
        return minerLongitudeLatitudeService.initMinerIp();
    }

    @ApiOperation(value = "扇区数据初始化-写入缓存")
    @PostMapping("/initSectorToRedis")
    public void initSectorToRedis(){
        sysSectorsWrapService.initSectorToRedis();
    }

    @ApiOperation(value = "重新计算扇区封装总时长")
    @PostMapping("/initSectorDuration")
    public void initSectorDuration(){
        sysSectorsWrapService.initSectorDuration();
    }

    @ApiOperation(value = "非平台矿工上报接口,仅在大屏显示")
    @PostMapping("/noPlatformMiner")
    public Result noPlatformMiner(@RequestBody String body){
        JSONObject json = JSON.parseObject(body);
        String minerId = json.getString("minerId");
        BigDecimal powerAvailable = json.getBigDecimal("powerAvailable");
        Double balanceMinerAccount = json.getDouble("balanceMinerAccount");
        Long totalBlocks = json.getLong("totalBlocks");
        Double workerBalance = json.getDouble("balanceWorkerAccount");
        JSONArray postAccounts = json.getJSONArray("controlAccounts");

        BigDecimal postBalance = BigDecimal.ZERO;
        for(int i = 0; i < postAccounts.size();i++){
            JSONObject post = postAccounts.getJSONObject(i);
            BigDecimal balance = post.getBigDecimal("balance");
            postBalance = postBalance.add(balance);
        }
        NoPlatformMiner noPlatformMiner = new NoPlatformMiner()
                .setMinerId(minerId)
                .setPowerAvailable(powerAvailable)
                .setBalanceMinerAccount(balanceMinerAccount)
                .setTotalBlocks(totalBlocks)
                .setWorkerBalance(workerBalance)
                .setPostBalance(postBalance.doubleValue())
                .setUpdateTime(LocalDateTime.now());
        return noPlatformMinerService.noPlatformMiner(noPlatformMiner);
    }

    @ApiOperation(value = "获取非平台矿工")
    @PostMapping("/findNoPlatformMiners")
    public Result findNoPlatformMiners(){
        return noPlatformMinerService.findNoPlatformMiners();
    }


}