package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysTransferRecord;
import com.mei.hui.miner.model.GetUserEarningInput;
import com.mei.hui.miner.model.PoolEarningVo;
import com.mei.hui.miner.model.SysTransferRecordWrap;
import com.mei.hui.miner.service.ISysTransferRecordService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 系统划转记录Controller
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Api(tags = "系统划转记录")
@RestController
@RequestMapping("/system/transfer")
public class SysTransferRecordController
{
    @Autowired
    private ISysTransferRecordService sysTransferRecordService;


    /**
     * 查询系统划转记录列表,普通用户
     */
    @ApiOperation(value = "查询系统划转记录列表,普通用户")
    @GetMapping("/list")
    public Map<String,Object> list(SysTransferRecord sysTransferRecord){
        return sysTransferRecordService.findTransferRecords(sysTransferRecord);
    }

    /**
     * 查询系统划转记录列表，管理员用户
     */
    @ApiOperation(value = "查询系统划转记录列表，管理员用户")
    @GetMapping("/listForAdmin")
    public Map<String,Object> listForAdmin(SysTransferRecord sysTransferRecord)
    {
        return sysTransferRecordService.selectSysTransferRecordListUserName(sysTransferRecord);
    }

    /**
     * 获取系统划转记录详细信息
     */
    @ApiOperation(value = "获取系统划转记录详细信息")
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return Result.success(sysTransferRecordService.selectSysTransferRecordById(id));
    }

    /**
     * 修改系统划转记录-管理员审核通过不通过
     */
    @PutMapping
    @ApiOperation(value = "修改系统划转记录")
    public Result edit(@RequestBody SysTransferRecord sysTransferRecord)
    {
        if(sysTransferRecord.getId() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"id字段不能为空");
        }
        SysTransferRecord trans = sysTransferRecordService.selectSysTransferRecordById(sysTransferRecord.getId());
        if (trans == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        int rows = sysTransferRecordService.updateSysTransferRecord(sysTransferRecord);

        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
     * 删除系统划转记录
     */
    @ApiOperation(value = "删除系统划转记录")
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        int rows = sysTransferRecordService.deleteSysTransferRecordByIds(ids);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
     * 查询用户收益
     */
    @ApiOperation(value = "查询用户收益",notes = "查询用户收益出参：\n" +
            "\n" +
            "totalEarning总收益\n" +
            "totalLockAward总锁仓收益\n" +
            "totalWithdraw用户总共已提取\n" +
            "availableEarning用户可提取金额\n" +
            "drawingEarning正在提币中的")
    @GetMapping("/getUserEarning")
    public Result getUserEarning(GetUserEarningInput input){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId() == currencyId){
            return sysTransferRecordService.getUserEarning(input);
        }else if(CurrencyEnum.CHIA.getCurrencyId() == currencyId){
            return sysTransferRecordService.getUserChiaEarning(input);
        }
        return null;
    }

    @GetMapping("/getPoolEarning")
    public Result getPoolEarning() {
        BigDecimal totalEarning = sysTransferRecordService.selectTotalEarning();
        BigDecimal todayEarning = sysTransferRecordService.selectTodayEarning();
        totalEarning = totalEarning==null? BigDecimal.valueOf(0):totalEarning;
        todayEarning = todayEarning==null? BigDecimal.valueOf(0):todayEarning;
        PoolEarningVo e = new PoolEarningVo();
        e.setTodayEarning(todayEarning);
        e.setTotalEarning(totalEarning);
        return Result.success(e);
    }

    /**
     * 用户提币：
     * 1、先校验现有余额是否 大于 将要提取的fil, 余额 - 带提币中的fil > 即将提取的fil
     */
    @ApiOperation(value = "用户提币")
    @PostMapping("/withdraw")
    public Result withdraw(@Validated  @RequestBody SysTransferRecordWrap sysTransferRecordWrap)
    {
        return sysTransferRecordService.withdraw(sysTransferRecordWrap);
    }
}
