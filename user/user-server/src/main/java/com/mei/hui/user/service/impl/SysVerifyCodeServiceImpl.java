package com.mei.hui.user.service.impl;

import com.mei.hui.user.mapper.SysVerifyCodeMapper;
import com.mei.hui.user.entity.SysVerifyCode;
import com.mei.hui.user.service.ISysVerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统验证码Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Service
public class SysVerifyCodeServiceImpl implements ISysVerifyCodeService
{
    @Autowired
    private SysVerifyCodeMapper sysVerifyCodeMapper;

    /**
     * 查询系统验证码
     * 
     * @param id 系统验证码ID
     * @return 系统验证码
     */
    @Override
    public SysVerifyCode selectSysVerifyCodeById(Long id)
    {
        return sysVerifyCodeMapper.selectSysVerifyCodeById(id);
    }

    /**
     * 查询系统验证码列表
     * 
     * @param sysVerifyCode 系统验证码
     * @return 系统验证码
     */
    @Override
    public List<SysVerifyCode> selectSysVerifyCodeList(SysVerifyCode sysVerifyCode)
    {
        return sysVerifyCodeMapper.selectSysVerifyCodeList(sysVerifyCode);
    }

    /**
     * 新增系统验证码
     * 
     * @param sysVerifyCode 系统验证码
     * @return 结果
     */
    @Override
    public int insertSysVerifyCode(SysVerifyCode sysVerifyCode)
    {
        sysVerifyCode.setCreateTime(LocalDateTime.now());
        return sysVerifyCodeMapper.insertSysVerifyCode(sysVerifyCode);
    }

    /**
     * 修改系统验证码
     * 
     * @param sysVerifyCode 系统验证码
     * @return 结果
     */
    @Override
    public int updateSysVerifyCode(SysVerifyCode sysVerifyCode)
    {
        sysVerifyCode.setUpdateTime(LocalDateTime.now());
        return sysVerifyCodeMapper.updateSysVerifyCode(sysVerifyCode);
    }

    /**
     * 批量删除系统验证码
     * 
     * @param ids 需要删除的系统验证码ID
     * @return 结果
     */
    @Override
    public int deleteSysVerifyCodeByIds(Long[] ids)
    {
        return sysVerifyCodeMapper.deleteSysVerifyCodeByIds(ids);
    }

    /**
     * 删除系统验证码信息
     * 
     * @param id 系统验证码ID
     * @return 结果
     */
    @Override
    public int deleteSysVerifyCodeById(Long id)
    {
        return sysVerifyCodeMapper.deleteSysVerifyCodeById(id);
    }

    @Override
    public SysVerifyCode selectSysVerifyCodeByUserId(Long userId) {
        return sysVerifyCodeMapper.selectSysVerifyCodeByUserId(userId);
    }
    /**
     * 查询验证码是否有效
     *
     * @param sysVerifyCode 系统验证码
     * @return 系统验证码
     */
    @Override
    public SysVerifyCode selectSysVerifyCodeByUserIdAndVerifyCode(SysVerifyCode sysVerifyCode)
    {
        return sysVerifyCodeMapper.selectSysVerifyCodeByUserIdAndVerifyCode(sysVerifyCode);
    }
}
