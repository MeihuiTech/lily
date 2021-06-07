package com.mei.hui.user.service;

import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.user.model.*;
import com.mei.hui.util.Result;

import java.util.List;
import java.util.Map;

public interface ISysUserService {

    Map<String,Object> getSysUserByNameAndPass(LoginBody loginBody);
    String selectUserRoleGroup(String userName);

    Map<String,Object> selectUserList(SelectUserListInput user);

    /**
    * 起亚币用户列表
    *
    * @description
    * @author shangbin
    * @date 2021/6/7 11:19
    * @param [user]
    * @return java.util.Map<java.lang.String,java.lang.Object>
    * @version v1.0.0
    */
    Map<String,Object> selectChiaUserList(SelectUserListInput user);

    SysUser selectUserById(Long userId);

    String checkUserNameUnique(String userName);

    String checkPhoneUnique(SysUser user);

    String checkEmailUnique(SysUser user);

    int insertUser(SysUser user);
    int updateUser(SysUser user);

    void checkUserAllowed(SysUser user);

    int deleteUserByIds(Long[] userIds);

    int resetPwd(SysUser user);

    int updateUserStatus(SysUser user);

    Result<List<SysUserOut>> findSysUserList(FindSysUserListInput req);

    SysUser getLoginUser();

    SysUser getUserById(Long userId);

    boolean updateUserAvatar(Long userId, String avatar);

    Map<String,Object> Impersonation(Long userId);

    /**
     * 用户模糊查询
     * @param req
     * @return
     */
    Result<List<FindSysUsersByNameVO>> findSysUsersByName(FindSysUsersByNameBO req);

    int updateUserProfile(SysUser user);

    Map<String,Object> updateProfile(SysUserBO user);

    Result updatePwd(String oldPassword, String newPassword);

    /**
    * 根据apiKey查询用户的userId
    *
    * @description
    * @author shangbin
    * @date 2021/5/26 11:20
    * @param [apiKey]
    * @return com.mei.hui.util.Result<java.lang.String>
    * @version v1.0.0
    */
    public Result<Long> findUserIdByApiKey(java.lang.String apiKey);

    Result<ChangeCurrencyVO> changeCurrency(ChangeCurrencyBO changeCurrencyBO);
}
