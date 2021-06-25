package com.mei.hui.user.service.impl;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.user.entity.SysMenu;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.model.RouterVo;
import com.mei.hui.user.service.ISysMenuService;
import com.mei.hui.user.service.LoginService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.IpUtils;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
    @Value("${server.port}")
    private String serverPort;
    @Autowired
    private RuoYiConfig ruoYiConfig;

    public Map<String,Object> getInfo(){
        Long userId = HttpRequestUtil.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        user.setPassword(null);
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        String avatar = user.getAvatar();
        if(StringUtils.isEmpty(user.getAvatar())){
            avatar = ruoYiConfig.getLogUrl();
        }
        user.setAvatar("/user-server/"+avatar);
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
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(HttpRequestUtil.getUserId());
        List<RouterVo> list = menuService.buildMenus(menus);
        return Result.success(list);
    }
}
