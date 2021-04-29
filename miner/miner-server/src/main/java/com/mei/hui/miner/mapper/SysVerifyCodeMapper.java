package com.mei.hui.miner.mapper;

import com.mei.hui.miner.model.SysVerifyCode;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统验证码Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Repository
public interface SysVerifyCodeMapper 
{
    /**
     * 查询系统验证码
     * 
     * @param id 系统验证码ID
     * @return 系统验证码
     */
    public SysVerifyCode selectSysVerifyCodeById(Long id);

    /**
     * 查询系统验证码列表
     * 
     * @param sysVerifyCode 系统验证码
     * @return 系统验证码集合
     */
    public List<SysVerifyCode> selectSysVerifyCodeList(SysVerifyCode sysVerifyCode);

    /**
     * 新增系统验证码
     * 
     * @param sysVerifyCode 系统验证码
     * @return 结果
     */
    public int insertSysVerifyCode(SysVerifyCode sysVerifyCode);

    /**
     * 修改系统验证码
     * 
     * @param sysVerifyCode 系统验证码
     * @return 结果
     */
    public int updateSysVerifyCode(SysVerifyCode sysVerifyCode);

    /**
     * 删除系统验证码
     * 
     * @param id 系统验证码ID
     * @return 结果
     */
    public int deleteSysVerifyCodeById(Long id);

    /**
     * 批量删除系统验证码
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysVerifyCodeByIds(Long[] ids);


    SysVerifyCode selectSysVerifyCodeByUserId(Long userId);
    /**
     * 查询验证码是否有效
     *
     * @param sysVerifyCode 系统验证码
     * @return 系统验证码
     */
    SysVerifyCode selectSysVerifyCodeByUserIdAndVerifyCode(SysVerifyCode sysVerifyCode);
}
