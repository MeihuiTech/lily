package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.entity.SysVerifyCode;
import com.mei.hui.miner.feign.vo.FindCodeByUserIdInput;
import com.mei.hui.miner.feign.vo.SysVerifyCodeInput;
import com.mei.hui.miner.service.ISysVerifyCodeService;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/code")
//@Api(tags = "验证码模块")
public class SysVerifyCodeController {
    @Autowired
    private ISysVerifyCodeService sysVerifyCodeService;
    /**
     * 根据 userId 获取用户信息
     * @return
     */
    @ApiOperation(value = "根据用户获取验证码")
    @PostMapping("/findCodeByUserId")
    public Result<SysVerifyCodeInput> findCodeByUserId(@RequestBody FindCodeByUserIdInput input){
        SysVerifyCode code = sysVerifyCodeService.selectSysVerifyCodeByUserId(input.getUserId());
        SysVerifyCodeInput sysVerifyCodeInput = new SysVerifyCodeInput();
        BeanUtils.copyProperties(code,sysVerifyCodeInput);
        return Result.success(sysVerifyCodeInput);
    }

    @ApiOperation(value = "新增验证码")
    @PostMapping("/insertSysVerifyCode")
    public Result insertSysVerifyCode(@RequestBody SysVerifyCodeInput input){
        SysVerifyCode sysVerifyCode = new SysVerifyCode();
        BeanUtils.copyProperties(input,sysVerifyCode);
        sysVerifyCodeService.insertSysVerifyCode(sysVerifyCode);
        return Result.OK;
    }



}
