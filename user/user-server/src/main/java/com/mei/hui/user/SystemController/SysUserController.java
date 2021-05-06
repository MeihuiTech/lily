package com.mei.hui.user.SystemController;

import com.mei.hui.config.smsConfig.SmsUtil;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysRole;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.entity.SysVerifyCode;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.user.model.SelectUserListInput;
import com.mei.hui.user.service.ISysRoleService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.user.service.ISysVerifyCodeService;
import com.mei.hui.util.AESUtil;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息
 * @author ruoyi
 */
@Api(value="用戶信息", tags = "用戶信息")
@RestController
@RequestMapping("/system/user")
public class SysUserController{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysVerifyCodeService sysVerifyCodeService;

    @RequestMapping("/getSysUser")
    public Result<SysUserOut> getSysUser(){
        SysUser sysUser = userService.getSysUser();
        SysUserOut sysUserOut = new SysUserOut();
        BeanUtils.copyProperties(sysUser,sysUserOut);
        return Result.success(sysUserOut);
    }

    /**
     * 批量获取用户信息
     * @param req
     * @return
     */
    @PostMapping("/findSysUserList")
    public Result<List<SysUserOut>> findSysUserList(@RequestBody FindSysUserListInput req){
        return userService.findSysUserList(req);
    }

    /**
     * 获取用户列表
     */
    @ApiOperation(value = "用户列表")
    @GetMapping("/list")
    public Map<String,Object> list(SelectUserListInput user){
        return userService.selectUserList(user);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @GetMapping(value = { "/", "/{userId}" })
    public Map<String,Object> getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());

        List<SysRole> roles = roleService.selectRoleAll();
        map.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        map.put("posts", null);
        if (userId != null)
        {
            map.put("data", userService.selectUserById(userId));
            map.put("postIds", null);
            map.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return map;
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysUser user){
        if ("1".equals(userService.checkUserNameUnique(user.getUserName()))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"登录账号已存在");
        }else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && "1".equals(userService.checkPhoneUnique(user))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"手机号码已存在");
        }else if (StringUtils.isNotEmpty(user.getEmail())
                && "1".equals(userService.checkEmailUnique(user))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"邮箱账号已存在");
        }
        SysUser userOut = userService.getSysUser();
        user.setCreateBy(userOut.getUserName());
        user.setPassword(AESUtil.encrypt(user.getPassword()));
        int rows = userService.insertUser(user);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysUser user){
        if (user.getUserId() != null && user.isAdmin()){
            throw MyException.fail(UserError.MYB_333333.getCode(),"不允许操作超级管理员用户");
        }
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && "1".equals(userService.checkPhoneUnique(user))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && "1".equals(userService.checkEmailUnique(user))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"邮箱账号已存在");
        }
        SysUser userOut = userService.getSysUser();
        user.setUpdateBy(userOut.getUserName());
        int rows = userService.updateUser(user);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    public Result remove(@PathVariable Long[] userIds){
        int rows = userService.deleteUserByIds(userIds);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPwd")
    public Result resetPwd(@RequestBody SysUser user){
        userService.checkUserAllowed(user);
        SysUser sysUser = userService.getSysUser();
        user.setPassword(AESUtil.encrypt(user.getPassword()));
        user.setUpdateBy(sysUser.getUserName());
        int rows = userService.resetPwd(user);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        SysUser sysUser = userService.getSysUser();
        user.setUpdateBy(sysUser.getUserName());
        int rows = userService.updateUserStatus(user);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    @PostMapping("/sendSms")
    public Result sendSms() {
        SysUser user = userService.getSysUser();
        SysVerifyCode code = sysVerifyCodeService.selectSysVerifyCodeByUserId(user.getUserId());
        if (code != null) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime date = code.getCreateTime();
            Duration duration = Duration.between(date, now);
            if (duration.toMinutes() < 1) {
                throw MyException.fail(UserError.MYB_333333.getCode(),"发送频繁，请稍后再试");
            }
        }
        int min = 123456;
        int max = 999999;
        Random r = new Random();
        String sms = String.valueOf(r.nextInt(max - min + 1) + min);
        if (SmsUtil.send(user.getPhonenumber(), sms)) {
            SysVerifyCode sysVerifyCode = new SysVerifyCode();
            sysVerifyCode.setVerifyCode(sms);
            sysVerifyCode.setUserId(user.getUserId());
            sysVerifyCode.setStatus(0);
            sysVerifyCode.setPhone(user.getPhonenumber());
            sysVerifyCode.setCreateTime(LocalDateTime.now());
            sysVerifyCode.setUpdateTime(LocalDateTime.now());
            sysVerifyCodeService.insertSysVerifyCode(sysVerifyCode);
            return Result.OK;
        } else {
            return Result.fail(UserError.MYB_333333.getCode(),"发送失败");
        }
    }

}
