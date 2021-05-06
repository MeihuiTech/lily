package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.miner.entity.SysTransferRecord;
import com.mei.hui.miner.entity.SysTransferRecordUserName;
import com.mei.hui.miner.mapper.SysTransferRecordMapper;
import com.mei.hui.miner.service.ISysTransferRecordService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.FindSysUserListInput;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统划转记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-08
 */
@Service
@Slf4j
public class SysTransferRecordServiceImpl implements ISysTransferRecordService
{
    @Autowired
    private SysTransferRecordMapper sysTransferRecordMapper;
    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * 查询系统划转记录
     * 
     * @param id 系统划转记录ID
     * @return 系统划转记录
     */
    @Override
    public SysTransferRecord selectSysTransferRecordById(Long id)
    {
        return sysTransferRecordMapper.selectSysTransferRecordById(id);
    }

    /**
     * 查询系统划转记录列表
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录
     */
    @Override
    public List<SysTransferRecord> selectSysTransferRecordList(SysTransferRecord sysTransferRecord)
    {
        return sysTransferRecordMapper.selectSysTransferRecordList(sysTransferRecord);
    }

    /**
     * 新增系统划转记录
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    @Override
    public int insertSysTransferRecord(SysTransferRecord sysTransferRecord)
    {
        sysTransferRecord.setCreateTime(LocalDateTime.now());
        return sysTransferRecordMapper.insertSysTransferRecord(sysTransferRecord);
    }

    /**
     * 修改系统划转记录
     * 
     * @param sysTransferRecord 系统划转记录
     * @return 结果
     */
    @Override
    public int updateSysTransferRecord(SysTransferRecord sysTransferRecord)
    {
        sysTransferRecord.setUpdateTime(LocalDateTime.now());
        return sysTransferRecordMapper.updateSysTransferRecord(sysTransferRecord);
    }

    /**
     * 批量删除系统划转记录
     * 
     * @param ids 需要删除的系统划转记录ID
     * @return 结果
     */
    @Override
    public int deleteSysTransferRecordByIds(Long[] ids)
    {
        return sysTransferRecordMapper.deleteSysTransferRecordByIds(ids);
    }

    /**
     * 删除系统划转记录信息
     * 
     * @param id 系统划转记录ID
     * @return 结果
     */
    @Override
    public int deleteSysTransferRecordById(Long id)
    {
        return sysTransferRecordMapper.deleteSysTransferRecordById(id);
    }

    /**
     * 获取用户已提取收益
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public Double selectTotalWithdrawByUserId(Long userId)
    {
        return sysTransferRecordMapper.selectTotalWithdrawByUserId(userId);
    }

    @Override
    public BigDecimal selectTotalEarning() {
        return sysTransferRecordMapper.selectTotalEarning();
    }

    @Override
    public BigDecimal selectTodayEarning() {
        return sysTransferRecordMapper.selectTodayEarning();
    }

    /**
     * 查询系统划转记录列表,加UserName
     *
     * @param sysTransferRecord 系统划转记录
     * @return 系统划转记录集合
     */
    @Override
    public Map<String,Object> selectSysTransferRecordListUserName(SysTransferRecord sysTransferRecord){
        LambdaQueryWrapper<SysTransferRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.setEntity(sysTransferRecord);
        IPage<SysTransferRecord> page = sysTransferRecordMapper.selectPage(new Page<>(sysTransferRecord.getPageNum(), sysTransferRecord.getPageSize()), queryWrapper);
        /**
         * 批量获取用户
         */
        List<Long> userids = page.getRecords().stream().map(v -> {
            return v.getUserId();
        }).collect(Collectors.toList());
        Map<Long, SysUserOut> userMaps = sysUserToMap(userids);

        page.getRecords().stream().forEach(v -> {
            SysUserOut user = userMaps.get(v.getUserId());
            v.setUserName(user.getUserName());
        });
        /**
         * 组装返回信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("rows", page.getRecords());
        map.put("total", page.getTotal());
        return map;

    }

    public Map<Long,SysUserOut>  sysUserToMap(List<Long> userids){
        FindSysUserListInput input = new FindSysUserListInput();
        input.setUserIds(userids);
        log.info("请求用户模块");
        Result<List<SysUserOut>> result = userFeignClient.findSysUserList(input);
        log.info("用户模块返回值:{}", JSON.toJSONString(result));
        Map<Long,SysUserOut> map = new HashMap<>();
        if(ErrorCode.MYB_000000.getCode().equals(result.getCode())){
            List<SysUserOut> users = result.getData();
            users.stream().forEach(v->{
                map.put(v.getUserId(),v);
            });
        }
        return map;
    }
}
