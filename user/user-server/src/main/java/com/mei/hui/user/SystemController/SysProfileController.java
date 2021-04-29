package com.mei.hui.user.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息 业务处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system")
public class SysProfileController{
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 个人信息
     */
    @GetMapping("/user/profile")
    public Map<String,Object> profile(){
        Long userId = HttpRequestUtil.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        Map<String,Object> result = new HashMap<>();
        result.put("code", ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());
        result.put("roleGroup", userService.selectUserRoleGroup(user.getUserName()));
        result.put("postGroup", userService.selectUserPostGroup(user.getUserName()));
        return result;
    }




}
