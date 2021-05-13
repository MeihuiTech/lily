package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.entity.SysSectorsWrap;
import com.mei.hui.miner.model.RequestMachineInfo;
import com.mei.hui.miner.model.RequestMinerInfo;
import com.mei.hui.miner.model.RequestSectorInfo;
import com.mei.hui.miner.service.ISysMachineInfoService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.miner.service.ISysSectorInfoService;
import com.mei.hui.miner.service.ISysSectorsWrapService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 矿工信息Controller
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
@Api(value="客户端上报信息", tags = "信息上报")
@RestController
@RequestMapping("/system/reported")
public class SysReportedController
{
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Autowired
    private ISysMachineInfoService sysMachineInfoService;

    @Autowired
    private ISysSectorsWrapService sysSectorsWrapService;

    @Autowired
    private UserFeignClient userService;

    /**
     * 新增矿工信息
     */
    @ApiOperation(value = "新增矿工")
    @PostMapping("miner")
    public Result miner(@Validated @RequestBody RequestMinerInfo sysMinerInfo)
    {
        SysUserOut sysUserInput = new SysUserOut();
        sysUserInput.setUserId(sysMinerInfo.getUserId());
        Result<SysUserOut> userResult = userService.getUserById(sysUserInput);
        if (!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())
                || userResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"获取用户失败");
        }
        SysMinerInfo miner  = sysMinerInfoService.selectSysMinerInfoByUserIdAndMinerId(sysMinerInfo.getUserId(), sysMinerInfo.getMinerId());
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
    @PostMapping("machine")
    public Result machine(@Validated @RequestBody RequestMachineInfo sysMachineInfo)
    {
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
    public Result sector(@Validated @RequestBody RequestSectorInfo sysSectorInfo)
    {
        int rows = sysSectorsWrapService.addSector(sysSectorInfo);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }



}