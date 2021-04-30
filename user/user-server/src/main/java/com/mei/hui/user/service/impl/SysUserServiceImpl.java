package com.mei.hui.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.*;
import com.mei.hui.user.mapper.*;
import com.mei.hui.user.model.LoginBody;
import com.mei.hui.user.model.SelectUserListInput;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysRoleMapper roleMapper;
    @Autowired
    protected SysUserMapper sysUserMapper;

    @Autowired
    private RedisUtil redisUtils;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    public Map<String,Object> getSysUserByNameAndPass(LoginBody loginBody){
        /**
         * 验证码校验
         */
        String verifyKey = Constants.CAPTCHA_CODE_KEY + loginBody.getUuid();
        String captcha = redisUtils.get(verifyKey);
        redisUtils.delete(verifyKey);
        if (captcha == null){
            throw  MyException.fail(UserError.MYB_333333.getCode(),"用户名或密码错误");
        }
        if (!loginBody.getCode().equalsIgnoreCase(captcha)){
            throw  MyException.fail(UserError.MYB_333333.getCode(),"用户名或密码错误");
        }
        /**
         * 用户校验
         */
        String passWord = AESUtil.encrypt(loginBody.getPassword());
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUserName,loginBody.getUsername());
        queryWrapper.eq(SysUser::getPassword,passWord);
        queryWrapper.eq(SysUser::getStatus,0);
        List<SysUser> sysUsers = sysUserMapper.selectList(queryWrapper);
        if(sysUsers.size() == 0){
            throw new MyException(ErrorCode.MYB_111111.getCode(),"用户和或密码错误");
        }
        SysUser sysUser = sysUsers.get(0);
        Map<String,Object> result = new HashMap<>();
        result.put("code",ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());

        String token = AESUtil.encrypt(sysUser.getUserId() + "");
        //生成token
        result.put(SystemConstants.TOKEN,AESUtil.encrypt(sysUser.getUserId()+""));
        //用户token 有效期30分钟
        redisUtils.set(token, JSON.toJSONString(sysUser),60*8, TimeUnit.MINUTES);
        return result;
    }

    /**
     * 查询用户所属角色组
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysRole role : list)
        {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString()))
        {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    public SysUser getSysUser(){
        Long userId = HttpRequestUtil.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        return sysUser;
    }

    /**
     * 根据条件分页查询用户列表
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public Map<String,Object> selectUserList(SelectUserListInput user)
    {
        SysUser sysUser = new SysUser();
        //BeanUtils.copyProperties(user,sysUser);
        Integer pageNum = user.getPageNum();
        Integer pageSize = user.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = sysUserMapper.selectUserList(sysUser);
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);

        //组装返回值
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("total",pageInfo.getTotal());
        map.put("rows",pageInfo.getList());
        return map;
    }

    /**
     * 通过用户ID查询用户
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId){
        return sysUserMapper.selectById(userId);
    }

    /**
     * 校验用户名称是否唯一
     * @param userName 用户名称
     * @return 结果
     */
    public String checkUserNameUnique(String userName)
    {
        int count = sysUserMapper.checkUserNameUnique(userName);
        if (count > 0)
        {
            return 1+"";
        }
        return "SysPermissionService";
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public String checkPhoneUnique(SysUser user)
    {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = sysUserMapper.checkPhoneUnique(user.getPhonenumber());
        if (info != null && info.getUserId().longValue() != userId.longValue())
        {
            return "1";
        }
        return "SysPermissionService";
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public String checkEmailUnique(SysUser user)
    {
        Long userId = user.getUserId() == null ? -1L : user.getUserId();
        SysUser info = sysUserMapper.checkEmailUnique(user.getEmail());
        if (info!=null && info.getUserId().longValue() != userId.longValue()){
            return "1";
        }
        return "SysPermissionService";
    }

    public int insertUser(SysUser user)
    {
        // 新增用户信息
        int rows = sysUserMapper.insert(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 新增用户角色信息
     *
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        Long[] roles = user.getRoleIds();
        if (roles!=null && roles.length > 0)
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roles)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0)
            {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 修改保存用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    @Transactional
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        return sysUserMapper.updateUser(user);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Transactional
    public int deleteUserByIds(Long[] userIds){
        for (Long userId : userIds){
            SysUser sysUser = new SysUser();
            sysUser.setUserId(userId);
            checkUserAllowed(sysUser);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        return sysUserMapper.deleteUserByIds(userIds);
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (user.getUserId()!=null && user.isAdmin()){
            throw MyException.fail(UserError.MYB_333333.getCode(),"不允许操作超级管理员用户");
        }
    }

    /**
     * 重置用户密码
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(SysUser user)
    {
        return sysUserMapper.updateUser(user);
    }

    /**
     * 修改用户状态
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(SysUser user)
    {
        return sysUserMapper.updateUser(user);
    }
}
