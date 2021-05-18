package com.mei.hui.miner.SystemController;


import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysReceiveAddress;
import com.mei.hui.miner.entity.SysVerifyCode;
import com.mei.hui.miner.model.SysReceiveAddressBO;
import com.mei.hui.miner.model.SysReceiveAddressVO;
import com.mei.hui.miner.service.ISysReceiveAddressService;
import com.mei.hui.miner.service.ISysVerifyCodeService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

/*
*
* @description 收款地址表
* @author shangbin
* @date 2021/5/14 10:37
* @version v1.0.0
*/
@Slf4j
@Api(value = "收款地址表",tags = "收款地址表")
@RestController
@RequestMapping("/system/receiveAddress")
public class SysReceiveAddressController {

    @Autowired
    private ISysReceiveAddressService sysReceiveAddressService;

    @Autowired
    private ISysVerifyCodeService sysVerifyCodeService;

    /*
    *
    * @description 新增收款地址
    * @author shangbin
    * @date 2021/5/14 10:49
    * @param [sysReceiveAddress]
    * @return com.mei.hui.util.Result
    * @version v1.0.0
    */
    @ApiOperation(value = "新增收款地址")
    @PostMapping("/add")
    public Result addReceiveAddress(@RequestBody SysReceiveAddressBO sysReceiveAddressBO) {
        Long userId = HttpRequestUtil.getUserId();
        if (userId != null && userId != 1L) {
            throw new MyException(MinerError.MYB_222222.getCode(),"没有权限");
        }

        // 校验验证码, 如果校验成功, 将验证码设置为已使用
        SysVerifyCode sysVerifyCode = new SysVerifyCode();
        sysVerifyCode.setUserId(userId);
        sysVerifyCode.setVerifyCode(sysReceiveAddressBO.getVerifyCode());
        SysVerifyCode sysVerifyCodeRet = sysVerifyCodeService.selectSysVerifyCodeByUserIdAndVerifyCode(sysVerifyCode);
        if (sysVerifyCodeRet == null) {
            return Result.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        sysVerifyCodeRet.setStatus(1);
        sysVerifyCodeRet.setUpdateTime(LocalDateTime.now());
        sysVerifyCodeService.updateSysVerifyCode(sysVerifyCodeRet);

        SysReceiveAddress sysReceiveAddress = new SysReceiveAddress();
        BeanUtils.copyProperties(sysReceiveAddressBO,sysReceiveAddress);
        sysReceiveAddress.setUserId(userId);
        sysReceiveAddress.setCreateTime(new Date());
        int rows = sysReceiveAddressService.addReceiveAddress(sysReceiveAddress);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
     * 编辑收款地址：先逻辑删除原来的，再新建一个新的，保留历史记录
     * @description
     * @author shangbin
     * @date 2021/5/14 10:49
     * @param [sysReceiveAddress]
     * @return com.mei.hui.util.Result
     * @version v1.0.0
     */
    @ApiOperation(value = "编辑收款地址")
    @PostMapping("/update")
    public Result updateReceiveAddress(@RequestBody SysReceiveAddressBO sysReceiveAddressBO) {
        Long userId = HttpRequestUtil.getUserId();
        if (userId != null && userId != 1L) {
            throw new MyException(MinerError.MYB_222222.getCode(),"没有权限");
        }

        // 校验验证码, 如果校验成功, 将验证码设置为已使用
        SysVerifyCode sysVerifyCode = new SysVerifyCode();
        sysVerifyCode.setUserId(userId);
        sysVerifyCode.setVerifyCode(sysReceiveAddressBO.getVerifyCode());
        SysVerifyCode sysVerifyCodeRet = sysVerifyCodeService.selectSysVerifyCodeByUserIdAndVerifyCode(sysVerifyCode);
        if (sysVerifyCodeRet == null) {
            return Result.fail(MinerError.MYB_222222.getCode(),"验证码错误");
        }
        sysVerifyCodeRet.setStatus(1);
        sysVerifyCodeRet.setUpdateTime(LocalDateTime.now());
        sysVerifyCodeService.updateSysVerifyCode(sysVerifyCodeRet);

        SysReceiveAddress dbSysReceiveAddress = sysReceiveAddressService.selectSysReceiveAddressById(sysReceiveAddressBO.getId());
        if (dbSysReceiveAddress == null) {
            return Result.fail(MinerError.MYB_222222.getCode(),"当前收款地址不存在");
        }

        SysReceiveAddress sysReceiveAddress = new SysReceiveAddress();
        BeanUtils.copyProperties(sysReceiveAddressBO,sysReceiveAddress);
        sysReceiveAddress.setUserId(userId);
        int rows = sysReceiveAddressService.updateReceiveAddress(sysReceiveAddress);
        return rows > 0 ? Result.OK : Result.fail(MinerError.MYB_222222.getCode(),"失败");
    }

    /**
    * 根据id查询没有被删除的收款地址
    *
    * @description
    * @author shangbin
    * @date 2021/5/14 14:12
    * @param []
    * @return com.mei.hui.util.Result
    * @version v1.0.0
    */
    @ApiOperation(value = "根据id查询没有被删除的收款地址")
    @GetMapping("/{id}")
    public Result selectSysReceiveAddressById(@PathVariable("id") Long id) {
        SysReceiveAddress dbSysReceiveAddress = sysReceiveAddressService.selectSysReceiveAddressById(id);
        if (dbSysReceiveAddress == null) {
            return Result.fail(MinerError.MYB_222222.getCode(),"当前收款地址不存在");
        }
        SysReceiveAddressVO sysReceiveAddressVO = new SysReceiveAddressVO();
        BeanUtils.copyProperties(dbSysReceiveAddress,sysReceiveAddressVO);
        return Result.success(sysReceiveAddressVO);
    }

}
