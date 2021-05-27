package com.mei.hui.miner.service;

import com.mei.hui.miner.entity.SysTransferRecord;
import com.mei.hui.miner.entity.SysTransferRecordUserName;
import com.mei.hui.miner.model.GetUserEarningInput;
import com.mei.hui.miner.model.SysTransferRecordWrap;
import com.mei.hui.util.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 系统划转记录Service接口
 *
 * @author ruoyi
 * @date 2021-03-08
 */
public interface ISysTransferRecordService
{
    /**
     * 查询系统划转记录
     *
     * @param id 系统划转记录ID
     * @return 系统划转记录
     */
    public SysTransferRecord selectSysTransferRecordById(Long id);

    /**
     * 查询系统划转记录列表
     *
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录集合
     */
    public List<SysTransferRecord> selectSysTransferRecordList(SysTransferRecord sysTransferRecord);

    /**
     * 新增系统划转记录
     *
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    public int insertSysTransferRecord(SysTransferRecord sysTransferRecord);

    /**
     * 修改系统划转记录
     *
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    public int updateSysTransferRecord(SysTransferRecord sysTransferRecord);

    /**
     * 批量删除系统划转记录
     *
     * @param ids 需要删除的系统划转记录ID
     * @return 结果
     */
    public int deleteSysTransferRecordByIds(Long[] ids);

    /**
     * 删除系统划转记录信息
     *
     * @param id 系统划转记录ID
     * @return 结果
     */
    public int deleteSysTransferRecordById(Long id);

    /**
     * 获取用户已提取收益
     *
     * @param userId 用户ID
     * @return 结果
     */
    Double selectTotalWithdrawByUserId(Long userId);

    BigDecimal selectTotalEarning();

    BigDecimal selectTodayEarning();

    /**
     * 查询系统划转记录列表,加UserName
     *
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录集合
     */
    Map<String,Object> selectSysTransferRecordListUserName(SysTransferRecord sysTransferRecord);

    /**
     * 用户提币
     * @param sysTransferRecordWrap
     * @return
     */
    Result withdraw(SysTransferRecordWrap sysTransferRecordWrap);

    Result getUserEarning(GetUserEarningInput input);

    Map<String,Object> findTransferRecords(SysTransferRecord sysTransferRecord);

    /**
     * 获取起亚币收益详情
     * @param input
     * @return
     */
    Result getUserChiaEarning(GetUserEarningInput input);

}
