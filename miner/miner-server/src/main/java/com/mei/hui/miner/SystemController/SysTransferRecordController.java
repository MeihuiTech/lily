package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysTransferRecord;
import com.mei.hui.miner.model.GetUserEarningInput;
import com.mei.hui.miner.model.PoolEarningVo;
import com.mei.hui.miner.model.SysTransferRecordWrap;
import com.mei.hui.miner.service.ISysTransferRecordService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 系统划转记录Controller
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@RestController
@RequestMapping("/system/transfer")
public class SysTransferRecordController
{
    @Autowired
    private ISysTransferRecordService sysTransferRecordService;


    /**
     * 查询系统划转记录列表,普通用户
     */
    @GetMapping("/list")
    public Map<String,Object> list(SysTransferRecord sysTransferRecord){
        return sysTransferRecordService.findTransferRecords(sysTransferRecord);
    }

    /**
     * 查询系统划转记录列表，管理员用户
     */
    @GetMapping("/listForAdmin")
    public Map<String,Object> listForAdmin(SysTransferRecord sysTransferRecord)
    {
        return sysTransferRecordService.selectSysTransferRecordListUserName(sysTransferRecord);
    }

    /**
     * 获取系统划转记录详细信息
     */
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        return Result.success(sysTransferRecordService.selectSysTransferRecordById(id));
    }

    /**
     * 修改系统划转记录
     */
    @PutMapping
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
	@DeleteMapping("/{ids}")
    public Result remove(@PathVariable Long[] ids)
    {
        int rows = sysTransferRecordService.deleteSysTransferRecordByIds(ids);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
     * 查询用户收益
     */
    @GetMapping("/getUserEarning")
    public Result getUserEarning(GetUserEarningInput input){
        return sysTransferRecordService.getUserEarning(input);
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
    @PostMapping("/withdraw")
    public Result withdraw(@Validated  @RequestBody SysTransferRecordWrap sysTransferRecordWrap)
    {
        return sysTransferRecordService.withdraw(sysTransferRecordWrap);
    }
}
