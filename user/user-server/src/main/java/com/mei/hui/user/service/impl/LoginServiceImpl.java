package com.mei.hui.user.service.impl;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.user.entity.SysMenu;
import com.mei.hui.user.entity.SysRole;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysRoleMapper;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.model.RouterVo;
import com.mei.hui.user.service.ISysMenuService;
import com.mei.hui.user.service.LoginService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.IpUtils;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService{
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private ISysMenuService menuService;
    @Value("${server.port}")
    private String serverPort;
    @Autowired
    private RuoYiConfig ruoYiConfig;
    @Autowired
    private SysRoleMapper roleMapper;

    public Map<String,Object> getInfo(){
        Long userId = HttpRequestUtil.getUserId();
        Long roleId = HttpRequestUtil.getRoleId();
        boolean isVisitor = HttpRequestUtil.isVisitor();
        SysUser user = sysUserMapper.selectById(userId);
        user.setPassword(null);
        // 角色集合
        List<SysRole> roles = roleMapper.selectRolePermissionByUserId(roleId);
        // 权限集合
        Set<String> permissions = menuService.selectMenuPermsByUserId(roleId);
        String avatar = user.getAvatar();
        if(StringUtils.isEmpty(user.getAvatar())){
            avatar = ruoYiConfig.getLogUrl();
        }


        user.setAdmin(isAdmin(roles));
        user.setAvatar("/user-server/"+avatar);
        Map<String,Object> result = new HashMap<>();
        result.put("code", ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());
        result.put("user", user);
        result.put("permissions", permissions);
        List<String> roleKeys = new ArrayList<>();
        if(isVisitor){
            //如果是游客rolekey 返回 isVisitor
            roleKeys.add("isVisitor");
        }else{
            roleKeys = roles.stream().map(v -> v.getRoleKey()).collect(Collectors.toList());
        }
        result.put("roles",roleKeys);
        return result;
    }

    /**
     * 通过权限判断是否是管理员
     * @param roles
     * @return
     */
    public boolean isAdmin(List<SysRole> roles){
        boolean flag = false;
        for (SysRole role : roles){
            if(role.getType() == 0){
                flag = true;
            }
            break;
        }
        return flag;
    }
    /**
     * 获取路由信息
     * @return 路由信息
     */
    public Result<List<RouterVo>> getRouters(){
        List<SysMenu> menus = menuService.selectMenuTreeByRoleId(HttpRequestUtil.getRoleId());
        List<RouterVo> list = menuService.buildMenus(menus);
        return Result.success(list);
    }
}
