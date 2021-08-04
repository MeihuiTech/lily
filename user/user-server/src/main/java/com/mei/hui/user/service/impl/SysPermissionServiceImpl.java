package com.mei.hui.user.service.impl;

import com.mei.hui.user.entity.SysRole;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysRoleMapper;
import com.mei.hui.user.service.ISysMenuService;
import com.mei.hui.user.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户权限处理
 * @author ruoyi
 */
@Service
public class SysPermissionServiceImpl {

    @Autowired
    private ISysRoleService roleService;
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取角色数据权限
     *
     * @param userId 用户id
     * @return 角色权限信息
     */
    public List<SysRole> getRolePermission(Long userId){
        List<SysRole> roles = roleMapper.selectRolePermissionByUserId(userId);
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param user 用户信息
     * @return 菜单权限信息
     */
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> perms = new HashSet<String>();
  /*      // 管理员拥有所有权限
        if (user.isAdmin())
        {
            perms.add("*:*:*");
        }
        else
        {*/
            perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
        //}
        return perms;
    }
}
