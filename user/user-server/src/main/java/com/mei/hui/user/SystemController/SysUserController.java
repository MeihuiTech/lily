package com.mei.hui.user.SystemController;

import com.mei.hui.config.AESUtil;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysRole;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.user.model.AddSysUserBO;
import com.mei.hui.user.model.SelectUserListInput;
import com.mei.hui.user.model.SmsSendBO;
import com.mei.hui.user.service.ISysRoleService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.user.service.SmsService;
import com.mei.hui.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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
    private SmsService smsService;
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private RuoYiConfig ruoYiConfig;
    /**
     * 根据 userId 获取用户信息
     * @return
     */
    @PostMapping("/getUserById")
    public Result<SysUserOut> getUserById(@RequestBody SysUserOut sysUserOut){
        if(sysUserOut == null || sysUserOut.getUserId() == null){
            throw MyException.fail(UserError.MYB_333333.getCode(),"请输入用户id");
        }
        SysUser sysUser = userService.getUserById(sysUserOut.getUserId());
        if(sysUser == null){
            throw MyException.fail(UserError.MYB_333333.getCode(),"用户不存在");
        }
        SysUserOut out = new SysUserOut();
        BeanUtils.copyProperties(sysUser,out);
        return Result.success(out);
    }

    /**
     * 获取当前登陆用户
     * @return
     */
    @PostMapping("/getLoginUser")
    public Result<SysUserOut> getLoginUser(){
        SysUser sysUser = userService.getLoginUser();
        SysUserOut out = new SysUserOut();
        BeanUtils.copyProperties(sysUser,out);
        return Result.success(out);
    }

    /**
     * 批量获取用户信息
     * @param req
     * @return
     */
    @RequestMapping(value = "/findSysUserList",method = RequestMethod.POST)
    public Result<List<SysUserOut>> findSysUserList(@RequestBody FindSysUserListInput req){
        return userService.findSysUserList(req);
    }
    /**
     * 获取所有有效用户
     * @return
     */
    @RequestMapping(value = "/findAllUser",method = RequestMethod.POST)
    public Result<List<SysUserOut>> findAllUser(){
        return userService.findAllUser();
    }

    /**
     * 模糊查询
     * @param req
     * @return
     */
    @PostMapping("/findSysUsersByName")
    public Result<List<FindSysUsersByNameVO>> findSysUsersByName(@RequestBody FindSysUsersByNameBO req){
        return userService.findSysUsersByName(req);
    }

    /**
     * 根据apiKey查询用户的userId
     *
     * @description
     * @author shangbin
     * @date 2021/5/26 11:18
     * @param
     * @return com.mei.hui.util.Result<java.lang.String>
     * @version v1.0.0
     */
    @PostMapping("/findUserIdByApiKey")
    public Result<Long> findUserIdByApiKey(@RequestParam("apiKey") String apiKey) {
        if (StringUtils.isEmpty(apiKey)){
            throw MyException.fail(UserError.MYB_333333.getCode(),"apiKey不能为空");
        }
        return userService.findUserIdByApiKey(apiKey);
    }

    /**
     * 获取用户列表
     */
    @ApiOperation(value = "用户列表")
    @GetMapping("/list")
    public Map<String,Object> list(SelectUserListInput user){
        Long currencyId = HttpRequestUtil.getCurrencyId();
        if(CurrencyEnum.FIL.getCurrencyId().equals(currencyId)){
            return userService.selectUserList(user);
        }else if(CurrencyEnum.XCH.getCurrencyId().equals(currencyId)){
            return userService.selectChiaUserList(user);
        }
        return null;
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
        if (userId != null){
            List<Integer> roleIds = roleService.selectRoleListByUserId(userId);
            SysUser sysUser= userService.selectUserById(userId);
            sysUser.setPassword(null);
            map.put("data", sysUser);
            map.put("postIds", null);
            map.put("roleIds",roleIds.size() > 0 ? roleIds.get(0):null);
        }
        return map;
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result add(@Validated @RequestBody AddSysUserBO addSysUserBO){
        SysUser user = new SysUser();
        BeanUtils.copyProperties(addSysUserBO,user);
        user.setRats(addSysUserBO.getRats());
        String phonenumber = user.getPhonenumber();
        if (StringUtils.isEmpty(phonenumber)){
            throw MyException.fail(UserError.MYB_333333.getCode(),"手机号不能为空");
        }
        if (!CommonUtil.isMobile(phonenumber)){
            throw MyException.fail(UserError.MYB_333333.getCode(),"手机号格式不正确");
        }

        if ("1".equals(userService.checkUserNameUnique(user.getUserName()))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"登录账号已存在");
        }else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && "1".equals(userService.checkPhoneUnique(user))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"手机号码已存在");
        }
        /**
         * 校验是否包含中文，6到20个字符
         */
        int length = user.getPassword().length();
        if(CommonUtil.isContainChinese(user.getPassword()) || length < 8 || length > 32){
            throw MyException.fail(UserError.MYB_333333.getCode(),"密码格式错误");
        }
        SysUser userOut = userService.getLoginUser();
        user.setCreateBy(userOut.getUserName());
        user.setPassword(AESUtil.encrypt(user.getPassword()));
        user.setApiKey(IdUtils.fastSimpleUUID());
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
        SysUser userOut = userService.getLoginUser();
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
        /**
         * 校验是否包含中文，6到20个字符
         */
        if(StringUtils.isEmpty(user.getPassword())){
            throw MyException.fail(UserError.MYB_333333.getCode(),"请输入密码");
        }
        int length = user.getPassword().length();
        if(CommonUtil.isContainChinese(user.getPassword()) || length < 8 || length > 32){
            throw MyException.fail(UserError.MYB_333333.getCode(),"密码格式错误");
        }
        SysUser sysUser = userService.getLoginUser();
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
        SysUser sysUser = userService.getLoginUser();
        user.setUpdateBy(sysUser.getUserName());
        int rows = userService.updateUserStatus(user);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 仅供提取收益发送验证码使用，其它业务不能使用
     * @return
     */
    @ApiOperation(value = "仅供提取收益发送验证码使用，其它业务不能使用")
    @PostMapping("/sendSms")
    public Result sendSms() {
        SmsSendBO smsSendBO = new SmsSendBO();
        smsSendBO.setServiceName(SmsServiceNameEnum.withdraw.name());
        return smsService.send(smsSendBO);
    }

    @ApiOperation(value = "冒充用户登录【鲍红建】")
    @GetMapping("/impersonate/{userId}")
    public Map<String,Object> Impersonation(@PathVariable(value = "userId", required = true) Long userId){
        return userService.Impersonation(userId);
    }

    /**
     * 1、一键下线功能，调动一键下线接口，向redis 存下线的用户id
     * 2、在网关鉴权时查redis，看看用户是否被下线，如果是则返回token超时，并删除缓存，否则鉴权通过
     * @param userId
     * @return
     */
    @ApiOperation(value = "一键下线【鲍红建】")
    @GetMapping("/offline/{userId}")
    public Result offline(@PathVariable Long userId){
        String offline = String.format(Constants.OfflineUser, userId);
        redisUtils.set(offline,null,ruoYiConfig.getJwtMinutes(),TimeUnit.MINUTES);
        return Result.OK;
    }


}
