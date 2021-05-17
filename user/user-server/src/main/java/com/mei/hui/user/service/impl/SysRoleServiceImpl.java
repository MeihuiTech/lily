package com.mei.hui.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysRole;
import com.mei.hui.user.entity.SysRoleMenu;
import com.mei.hui.user.mapper.SysRoleMapper;
import com.mei.hui.user.mapper.SysRoleMenuMapper;
import com.mei.hui.user.mapper.SysUserRoleMapper;
import com.mei.hui.user.service.ISysRoleService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.*;

/**
 * 角色 业务层处理
 * @author ruoyi
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService{
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    /**
     * 根据条件分页查询角色数据
     * 
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public Map<String,Object> selectRoleList(SysRole role)
    {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotEmpty(role.getRoleName())){
            queryWrapper.like(SysRole::getRoleName,role.getRoleName());
        }
        if(StringUtils.isNotEmpty(role.getStatus())){
            queryWrapper.eq(SysRole::getStatus,role.getStatus());
        }
        if(StringUtils.isNotEmpty(role.getRoleKey())){
            queryWrapper.like(SysRole::getRoleKey,role.getRoleKey());
        }
        if(role.getParams() != null){
            String beginTime = (String) role.getParams().get("beginTime");
            String endTime = (String) role.getParams().get("endTime");
            if(StringUtils.isNotEmpty(beginTime)){
                queryWrapper.ge(SysRole::getCreateTime,beginTime);
            }
            if(StringUtils.isNotEmpty(endTime)){
                queryWrapper.le(SysRole::getCreateTime,endTime);
            }
        }
        IPage<SysRole> page = roleMapper.selectPage(new Page<>(role.getPageNum(), role.getPageSize()), queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",page.getRecords());
        map.put("total",page.getTotal());
        return map;
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId)
    {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms){
            if (perm != null){
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll()
    {
        return roleMapper.selectRoleAll();
    }

    /**
     * 根据用户ID获取角色选择框列表
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Integer> selectRoleListByUserId(Long userId){
        return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * 通过角色ID查询角色
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long roleId)
    {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleNameUnique(SysRole role)
    {
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(role.getRoleId() != null){
            lambdaQueryWrapper.ne(SysRole::getRoleId,role.getRoleId());
        }
        lambdaQueryWrapper.eq(SysRole::getRoleName,role.getRoleName());
        List<SysRole> list = roleMapper.selectList(lambdaQueryWrapper);
        if (list.size() > 0)
        {
            return 1+"";
        }
        return 0+"";
    }

    /**
     * 校验角色权限是否唯一
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public String checkRoleKeyUnique(SysRole role){
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(role.getRoleId() != null){
            lambdaQueryWrapper.ne(SysRole::getRoleId,role.getRoleId());
        }
        lambdaQueryWrapper.eq(SysRole::getRoleKey,role.getRoleKey());
        List<SysRole> list = roleMapper.selectList(lambdaQueryWrapper);
        if (list.size() > 0)
        {
            return 1+"";
        }
        return 0+"";
    }

    /**
     * 校验角色是否允许操作
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role) {
        if (role.getRoleId() != null && role.isAdmin()) {
            throw new MyException(UserError.MYB_333333.getCode(),"不允许操作超级管理员角色");
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId) {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 新增保存角色信息
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertRole(SysRole role) {
        // 新增角色信息
        roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateRole(SysRole role) {
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role) {
        return roleMapper.updateRole(role);
    }

    /**
     * 修改数据权限信息
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int authDataScope(SysRole role) {
        // 修改角色信息
        return roleMapper.updateRole(role);
    }

    /**
     * 新增角色菜单信息
     * 
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0) {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }



    /**
     * 通过角色ID删除角色
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleById(Long roleId) {
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        return roleMapper.deleteRoleById(roleId);
    }

    /**
     * 批量删除角色信息
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            SysRole sysUser = SysRole.builder().roleId(roleId).build();
            //检查是否是超级管理员角色，是，则不允许删除
            checkRoleAllowed(sysUser);
            //已分配的角色不允许删除
            if (countUserRoleByRoleId(roleId) > 0)
            {
                SysRole role = selectRoleById(roleId);
                throw new MyException(UserError.MYB_333333.getCode(),String.format("%1$s已分配,不能删除",role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenu(roleIds);
        return roleMapper.deleteBatchIds(Arrays.asList(roleIds));
    }



}
