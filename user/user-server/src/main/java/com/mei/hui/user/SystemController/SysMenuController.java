package com.mei.hui.user.SystemController;

import com.alibaba.csp.sentinel.cluster.TokenService;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysMenu;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.service.ISysMenuService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    public Result list(SysMenu menu)
    {
        SysUser user = sysUserService.getSysUser();
        List<SysMenu> menus = menuService.selectMenuList(menu, user.getUserId());
        return Result.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    public Result getInfo(@PathVariable Long menuId)
    {
        return Result.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public Result treeselect(SysMenu menu)
    {
        SysUser user = sysUserService.getSysUser();
        List<SysMenu> menus = menuService.selectMenuList(menu, user.getUserId());
        return Result.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public Map<String,Object> roleMenuTreeselect(@PathVariable("roleId") Long roleId){
        SysUser user = sysUserService.getSysUser();
        List<SysMenu> menus = menuService.selectMenuList(user.getUserId());
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        map.put("menus", menuService.buildMenuTreeSelect(menus));
        return map;
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysMenu menu){
        if ("1".equals(menuService.checkMenuNameUnique(menu))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"菜单名称已存在");
        }else if ("0".equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)){
            throw MyException.fail(UserError.MYB_333333.getCode(),"地址必须以http(s)://开头");
        }
        SysUser user = sysUserService.getSysUser();
        menu.setCreateBy(user.getUserName());
        int rows = menuService.insertMenu(menu);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 修改菜单
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysMenu menu){
        if (Constants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"菜单名称已存在");
        }else if (Constants.YES_FRAME.equals(menu.getIsFrame())
                && !StringUtils.startsWithAny(menu.getPath(), Constants.HTTP, Constants.HTTPS)){
            throw MyException.fail(UserError.MYB_333333.getCode(),"地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"上级菜单不能选择自己");
        }
        SysUser user = sysUserService.getSysUser();
        menu.setUpdateBy(user.getUserName());
        int rows = menuService.updateMenu(menu);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public Result remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"菜单已分配,不允许删除");
        }
        int rows = menuService.deleteMenuById(menuId);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }
}