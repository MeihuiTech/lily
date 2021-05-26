package com.mei.hui.miner.SystemController;

import com.mei.hui.config.CommonUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.model.RequestMachineInfo;
import com.mei.hui.miner.model.RequestMinerInfo;
import com.mei.hui.miner.model.RequestSectorInfo;
import com.mei.hui.miner.service.ISysMachineInfoService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.miner.service.ISysSectorsWrapService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 矿工信息Controller
 * 
 * @author ruoyi
 * @date 2021-05-26
 */
@Api(value="客户端上报信息", tags = "信息上报")
@RestController
@RequestMapping("/v2/system/reported")
public class SysReportedNewController
{
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Autowired
    private ISysMachineInfoService sysMachineInfoService;

    @Autowired
    private ISysSectorsWrapService sysSectorsWrapService;

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * 新增矿工信息
     */
    @ApiOperation(value = "新增矿工")
    @PostMapping("/miner")
    public Result miner(@RequestBody RequestMinerInfo sysMinerInfo)
    {
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String apiKey = httpServletRequest.getHeader(SystemConstants.APIKEY);
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前用户不存在");
        }
        Long userId = userIdResult.getData();

        SysUserOut sysUserInput = new SysUserOut();
        sysUserInput.setUserId(userId);
        Result<SysUserOut> userResult = userFeignClient.getUserById(sysUserInput);
        if (!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())
                || userResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"获取用户失败");
        }
        SysMinerInfo miner  = sysMinerInfoService.selectSysMinerInfoByUserIdAndMinerId(userId, sysMinerInfo.getMinerId());
        if (miner == null) {
            int rows = sysMinerInfoService.insertSysMinerInfo(sysMinerInfo);
            return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
        } else {
            sysMinerInfo.setId(miner.getId());
            int rows = sysMinerInfoService.updateSysMinerInfo(sysMinerInfo);
            return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
        }
    }


    /**
     * 新增矿机信息
     */
    @ApiOperation(value = "新增矿机")
    @PostMapping("/machine")
    public Result machine(@RequestBody RequestMachineInfo sysMachineInfo)
    {
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String apiKey = httpServletRequest.getHeader(SystemConstants.APIKEY);
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前用户不存在");
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
            throw MyException.fail(MinerError.MYB_222222.getCode(),"当前用户不存在");
        }

        int rows = sysSectorsWrapService.addSector(sysSectorInfo);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }



}