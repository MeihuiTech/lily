package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysTotalEarning;
import com.mei.hui.miner.entity.SysTransferRecord;
import com.mei.hui.miner.model.EarningVo;
import com.mei.hui.miner.model.GetUserEarningInput;
import com.mei.hui.miner.model.PoolEarningVo;
import com.mei.hui.miner.model.SysTransferRecordWrap;
import com.mei.hui.miner.entity.SysVerifyCode;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.miner.service.ISysTransferRecordService;
import com.mei.hui.miner.service.ISysVerifyCodeService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ISysVerifyCodeService sysVerifyCodeService;

    /**
     * 查询系统划转记录列表
     */
    @GetMapping("/list")
    public Map<String,Object> list(SysTransferRecord sysTransferRecord)
    {
        Result<SysUserOut> userResult = userFeignClient.getLoginUser();
        if(!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())){
            throw MyException.fail(userResult.getCode(),userResult.getMsg());
        }
        Long userId = userResult.getData().getUserId();
        sysTransferRecord.setUserId(userId);
        return sysTransferRecordService.selectSysTransferRecordListUserName(sysTransferRecord);
    }

    /**
     * 查询系统划转记录列表
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
        EarningVo earningVo = new EarningVo(0.0, 0.0, 0.0, 0.0);
        Result<SysUserOut> userResult = userFeignClient.getLoginUser();
        if(!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())){
            throw MyException.fail(userResult.getCode(),userResult.getMsg());
        }
        Long userId = userResult.getData().getUserId();
        //1. 通过miner_id从sys_miner_info表中获取总收益和总锁仓收益
        SysTotalEarning sysTotalEarning = sysMinerInfoService.selectTotalEarningAndAwardByUserId(input.getMinerId());
        if (sysTotalEarning == null) {
            earningVo.setTotalEarning(BigDecimalUtil
                    .formatFour(new BigDecimal(earningVo.getTotalEarning())).doubleValue());
            earningVo.setTotalLockAward(BigDecimalUtil
                    .formatFour(new BigDecimal(earningVo.getTotalLockAward())).doubleValue());
            return Result.success(earningVo);
        }
        earningVo.setTotalEarning(BigDecimalUtil
                .formatFour(new BigDecimal(sysTotalEarning.getTotalEarning())).doubleValue());
        earningVo.setTotalLockAward(BigDecimalUtil
                .formatFour(new BigDecimal(sysTotalEarning.getTotalLockAward())).doubleValue());
        //2. 通过miner_id从sys_transfer_record表中获取总的已提取收益,
        Double totalWithdraw = sysTransferRecordService.selectTotalWithdrawByUserId(userId);
        if (totalWithdraw == null) {
            return Result.success(earningVo);
        }
        earningVo.setTotalWithdraw(totalWithdraw);
        //3. 根据公式: 总收益 - 锁仓收益 - 已提取收益 = 可提取收益
        double availableEarning = sysTotalEarning.getTotalEarning() - sysTotalEarning.getTotalLockAward() - totalWithdraw;
        earningVo.setAvailableEarning(BigDecimalUtil
                .formatFour(new BigDecimal(availableEarning)).doubleValue());
        return Result.success(earningVo);
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
        Result<SysUserOut> userResult = userFeignClient.getLoginUser();
        if(!ErrorCode.MYB_000000.getCode().equals(userResult.getCode())){
            throw MyException.fail(userResult.getCode(),userResult.getMsg());
        }
        SysUserOut user = userResult.getData();
        BigDecimal fee = user.getFeeRate().multiply(sysTransferRecordWrap.getAmount());
        sysTransferRecordWrap.setFee(fee);

        //1. 校验验证码, 如果校验成功, 将验证码设置为已使用
        Long userId = user.getUserId();
        SysVerifyCode sysVerifyCode = new SysVerifyCode();
        sysVerifyCode.setUserId(userId);
        sysVerifyCode.setVerifyCode(sysTransferRecordWrap.getVerifyCode());

        SysVerifyCode sysVerifyCodeRet = sysVerifyCodeService.selectSysVerifyCodeByUserIdAndVerifyCode(sysVerifyCode);
        if (sysVerifyCodeRet == null) {
            return Result.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        sysVerifyCodeRet.setStatus(1);
        sysVerifyCodeRet.setUpdateTime(LocalDateTime.now());
        sysVerifyCodeService.updateSysVerifyCode(sysVerifyCodeRet);

        //2. 验证通过后, 记录提币申请
        SysTransferRecord sysTransferRecord = new SysTransferRecord();
        BeanUtils.copyProperties(sysTransferRecordWrap, sysTransferRecord);
        sysTransferRecord.setUserId(userId);
        sysTransferRecord.setCreateTime(LocalDateTime.now());
        sysTransferRecord.setUpdateTime(LocalDateTime.now());
        sysTransferRecord.setStatus(0);
        int rows = sysTransferRecordService.insertSysTransferRecord(sysTransferRecord);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }
}
