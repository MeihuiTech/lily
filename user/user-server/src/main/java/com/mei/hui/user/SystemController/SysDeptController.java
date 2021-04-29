package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysDept;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.service.ISysDeptService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 部门信息
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController{
    @Autowired
    private ISysDeptService deptService;
    @Autowired
    private ISysUserService userService;
    /**
     * 获取部门列表
     */
    @GetMapping("/list")
    public Result list(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.success(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @GetMapping("/list/exclude/{deptId}")
    public Result excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Iterator<SysDept> it = depts.iterator();
        while (it.hasNext())
        {
            SysDept d = (SysDept) it.next();
            if (d.getDeptId().intValue() == deptId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""))
            {
                it.remove();
            }
        }
        return Result.success(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @GetMapping(value = "/{deptId}")
    public Result getInfo(@PathVariable Long deptId)
    {
        return Result.success(deptService.selectDeptById(deptId));
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    public Result treeselect(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.success(deptService.buildDeptTreeSelect(depts));
    }

    /**
     * 加载对应角色部门列表树
     */
    @GetMapping(value = "/roleDeptTreeselect/{roleId}")
    public Map<String,Object> roleDeptTreeselect(@PathVariable("roleId") Long roleId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        map.put("depts", deptService.buildDeptTreeSelect(depts));
        return map;
    }

    /**
     * 新增部门
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysDept dept)
    {
        SysUser sysUser = userService.getSysUser();
        if ("1".equals(deptService.checkDeptNameUnique(dept)))
        {
            return Result.fail(UserError.MYB_333333.getCode(),"部门名称已存在");
        }
        dept.setCreateBy(sysUser.getUserName());
        int rows = deptService.insertDept(dept);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 修改部门
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysDept dept){
        if ("1".equals(deptService.checkDeptNameUnique(dept))){
            return Result.fail(UserError.MYB_333333.getCode(),"部门名称已存在");
        }else if (dept.getParentId().equals(dept.getDeptId())){
            return Result.fail(UserError.MYB_333333.getCode(), "上级部门不能是自己");
        }else if (StringUtils.equals("1", dept.getStatus())
                && deptService.selectNormalChildrenDeptById(dept.getDeptId()) > 0){
            return Result.fail(UserError.MYB_333333.getCode(),"该部门包含未停用的子部门！");
        }
        SysUser sysUser = userService.getSysUser();
        dept.setUpdateBy(sysUser.getUserName());
        int rows = deptService.updateDept(dept);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{deptId}")
    public Result remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            return Result.fail(UserError.MYB_333333.getCode(),"存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return Result.fail(UserError.MYB_333333.getCode(),"部门存在用户,不允许删除");
        }
        int rows = deptService.deleteDeptById(deptId);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }
}
