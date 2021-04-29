package com.mei.hui.user.service.impl;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.user.entity.SysMenu;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.model.RouterVo;
import com.mei.hui.user.service.ISysMenuService;
import com.mei.hui.user.service.LoginService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService{

    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysPermissionServiceImpl permissionService;

    @Autowired
    private ISysMenuService menuService;

    public Map<String,Object> getInfo(){
        Long userId = HttpRequestUtil.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);

        Map<String,Object> result = new HashMap<>();
        result.put("code", ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());
        result.put("user", user);
        result.put("roles", roles);
        result.put("permissions", permissions);
        return result;
    }

    /**
     * 获取路由信息
     * @return 路由信息
     */
    public Result<List<RouterVo>> getRouters(){
        SysUser user = sysUserMapper.selectById(HttpRequestUtil.getUserId());
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
        List<RouterVo> list = menuService.buildMenus(menus);
        return Result.success(list);
    }
}
