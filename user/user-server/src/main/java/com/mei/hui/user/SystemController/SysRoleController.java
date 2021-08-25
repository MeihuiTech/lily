package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysRole;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.feign.vo.VisitRoleBO;
import com.mei.hui.user.service.ISysRoleService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 角色信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController
{
    @Autowired
    private ISysRoleService roleService;

    
    @Autowired
    private ISysUserService userService;

    @GetMapping("/list")
    public Map<String,Object> list(SysRole role){
        return roleService.selectRoleList(role);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @GetMapping(value = "/{roleId}")
    public Result getInfo(@PathVariable Long roleId)
    {
        return Result.success(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysRole role){
        if ("1".equals(roleService.checkRoleNameUnique(role))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"角色名称已存在");
        }
        else if ("1".equals(roleService.checkRoleKeyUnique(role))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"角色权限已存在");
        }
        SysUser user = userService.getLoginUser();
        role.setCreateBy(user.getUserName());
        int rows = roleService.insertRole(role);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");

    }

    /**
     * 修改保存角色
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysRole role){
        //roleService.checkRoleAllowed(role);
        if ("1".equals(roleService.checkRoleNameUnique(role))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"角色名称已存在");
        }else if ("1".equals(roleService.checkRoleKeyUnique(role))){
            throw MyException.fail(UserError.MYB_333333.getCode(),"角色权限已存在");
        }
        SysUser user = userService.getLoginUser();
        role.setUpdateBy(user.getUserName());
        if (roleService.updateRole(role) > 0){
            return Result.OK;
        }
        return Result.fail(UserError.MYB_333333.getCode(),"请联系管理员");
    }

    /**
     * 修改保存数据权限
     */
    @PutMapping("/dataScope")
    public Result dataScope(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        return Result.success(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @PutMapping("/changeStatus")
    public Result changeStatus(@RequestBody SysRole role)
    {
        SysUser user = userService.getLoginUser();
        roleService.checkRoleAllowed(role);
        role.setUpdateBy(user.getUserName());
        int rows = roleService.updateRoleStatus(role);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleIds}")
    public Result remove(@PathVariable Long[] roleIds)
    {
        int rows = roleService.deleteRoleByIds(roleIds);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 获取角色选择框列表
     */
    @GetMapping("/optionselect")
    public Result optionselect()
    {
        return Result.success(roleService.selectRoleAll());
    }


    @ApiOperation(value = "查询游客角色状态")
    @PostMapping("/getVisitRoleState")
    public Result<VisitRoleBO> getVisitRoleState(){
        return roleService.getVisitRoleState();
    }
}
