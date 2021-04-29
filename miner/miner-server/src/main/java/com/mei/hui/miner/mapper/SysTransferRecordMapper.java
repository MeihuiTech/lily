package com.mei.hui.miner.mapper;

import com.mei.hui.miner.entity.SysTransferRecord;
import com.mei.hui.miner.entity.SysTransferRecordUserName;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

/**
 * 系统划转记录Mapper接口
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Repository
public interface SysTransferRecordMapper 
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
     * 删除系统划转记录
     * 
     * @param id 系统划转记录ID
     * @return 结果
     */
    public int deleteSysTransferRecordById(Long id);

    /**
     * 批量删除系统划转记录
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysTransferRecordByIds(Long[] ids);

    /**
     * 获取用户已提取收益
     *
     * @param userId 用户ID
     * @return 结果
     */
    Double selectTotalWithdrawByUserId(@Param("userId") Long userId);

    BigDecimal selectTodayEarning();

    BigDecimal selectTotalEarning();

    /**
     * 查询系统划转记录列表,加UserName
     *
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录集合
     */
    List<SysTransferRecordUserName> selectSysTransferRecordListUserName(SysTransferRecord sysTransferRecord);
}
