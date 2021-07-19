package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.entity.SysMachineInfo;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.MachineInfoTypeCountVO;
import com.mei.hui.miner.feign.vo.MachineInfoTypeOnlineVO;
import com.mei.hui.miner.mapper.SysMachineInfoMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.model.RequestMachineInfo;
import com.mei.hui.miner.service.ISysMachineInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 矿机信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
@Slf4j
@Service
public class SysMachineInfoServiceImpl implements ISysMachineInfoService
{
    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;
    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;

    /**
     * 查询矿机信息
     * 
     * @param id 矿机信息ID
     * @return 矿机信息
     */
    @Override
    public SysMachineInfo selectSysMachineInfoById(Long id)
    {
        return sysMachineInfoMapper.selectSysMachineInfoById(id);
    }

    /**
     * 查询矿机信息列表
     * 
     * @param sysMachineInfo 矿机信息
     * @return 矿机信息
     */
    @Override
    public List<SysMachineInfo> selectSysMachineInfoList(SysMachineInfo sysMachineInfo)
    {
        return sysMachineInfoMapper.selectSysMachineInfoList(sysMachineInfo);
    }

    /**
     * 新增矿机信息
     * 
     * @param sysMachineInfo 矿机信息
     * @return 结果
     */
    @Override
    public int insertSysMachineInfo(SysMachineInfo sysMachineInfo)
    {
        sysMachineInfo.setUpdateTime(LocalDateTime.now());
        sysMachineInfo.setCreateTime(LocalDateTime.now());
        return sysMachineInfoMapper.insertSysMachineInfo(sysMachineInfo);
    }

    /**
     * 修改矿机信息
     * 
     * @param sysMachineInfo 矿机信息
     * @return 结果
     */
    @Override
    public int updateSysMachineInfo(SysMachineInfo sysMachineInfo)
    {
        sysMachineInfo.setUpdateTime(LocalDateTime.now());
        return sysMachineInfoMapper.updateSysMachineInfo(sysMachineInfo);
    }

    /**
     * 批量删除矿机信息
     * 
     * @param ids 需要删除的矿机信息ID
     * @return 结果
     */
    @Override
    public int deleteSysMachineInfoByIds(Long[] ids)
    {
        return sysMachineInfoMapper.deleteSysMachineInfoByIds(ids);
    }

    /**
     * 删除矿机信息信息
     * 
     * @param id 矿机信息ID
     * @return 结果
     */
    @Override
    public int deleteSysMachineInfoById(Long id)
    {
        return sysMachineInfoMapper.deleteSysMachineInfoById(id);
    }

    @Override
    public SysMachineInfo selectSysMachineInfoByMinerAndHostname(String minerId, String hostname) {
        return sysMachineInfoMapper.selectSysMachineInfoByMinerAndHostname(minerId,hostname);
    }

    @Override
    public Long countByMinerId(String minerId) {
        return sysMachineInfoMapper.countByMinerId(minerId);
    }

    /**
     * 获取N条记录
     * @param offset 起始位置
     * @param rowCount 条数
     * @return 结果
     */
    @Override
    public List<SysMachineInfo> selectSysMachineInfoByLimit(Integer offset, Integer rowCount) {
        return sysMachineInfoMapper.selectSysMachineInfoByLimit(offset, rowCount);
    }

    /*批量新增矿机*/
    @Override
    public int insertSysMachineInfoList(List<RequestMachineInfo> requestMachineInfoList) {
        int count = 0;
        for (RequestMachineInfo requestMachineInfo: requestMachineInfoList) {
            SysMachineInfo machine = selectSysMachineInfoByMinerAndHostname(requestMachineInfo.getMinerId(),requestMachineInfo.getHostname());
            requestMachineInfo.setOnline(1);
            if (machine == null) {
                int rows = insertSysMachineInfo(requestMachineInfo);
                count += rows;
            } else {
                requestMachineInfo.setId(machine.getId());
                int rows = updateSysMachineInfo(requestMachineInfo);
                count += rows;
            }
        }
        return count;
    }

    /*查询各种矿机类型的数量*/
    @Override
    public MachineInfoTypeCountVO selectMachineInfoTypeCountById(Long id) {
        SysMinerInfo miner = sysMinerInfoMapper.selectSysMinerInfoById(id);
        log.info("矿工信息：【{}】", JSON.toJSON(miner));
        if (miner == null) {
            return null;
        }

        MachineInfoTypeCountVO machineInfoTypeCountVO = new MachineInfoTypeCountVO();
        List<MachineInfoTypeOnlineVO> machineInfoTypeOnlineVOList = sysMachineInfoMapper.selectMachineInfoTypeOnlineCountList(miner.getMinerId());
        log.info("按照机器类型、是否在线分组查询矿机信息表的数量出参：【{}】", JSON.toJSON(machineInfoTypeOnlineVOList));

        // 赋默认值
        machineInfoTypeCountVO.setMinerOnlineMachineCount(0);
        machineInfoTypeCountVO.setMinerOfflineMachineCount(0);
        machineInfoTypeCountVO.setPostOnlineMachineCount(0);
        machineInfoTypeCountVO.setPostOfflineMachineCount(0);
        machineInfoTypeCountVO.setCtwoOnlineMachineCount(0);
        machineInfoTypeCountVO.setCtwoOfflineMachineCount(0);
        machineInfoTypeCountVO.setSealOnlineMachineCount(0);
        machineInfoTypeCountVO.setSealOfflineMachineCount(0);

        if (machineInfoTypeOnlineVOList != null && machineInfoTypeOnlineVOList.size() > 0) {
            for (MachineInfoTypeOnlineVO machineInfoTypeOnlineVO : machineInfoTypeOnlineVOList) {
                log.info("按照机器类型、是否在线分组查询矿机信息表的数量：【{}】", machineInfoTypeOnlineVO);
                if (Constants.MACHINETYPEMINER.equals(machineInfoTypeOnlineVO.getMachineType())) {
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())) {
                        machineInfoTypeCountVO.setMinerOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                    } else {
                        machineInfoTypeCountVO.setMinerOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                    }
                } else if (Constants.MACHINETYPEPOST.equals(machineInfoTypeOnlineVO.getMachineType())) {
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())) {
                        machineInfoTypeCountVO.setPostOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                    } else {
                        machineInfoTypeCountVO.setPostOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                    }
                } else if (Constants.MACHINETYPECTWO.equals(machineInfoTypeOnlineVO.getMachineType())) {
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())) {
                        machineInfoTypeCountVO.setCtwoOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                    } else {
                        machineInfoTypeCountVO.setCtwoOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                    }
                } else if (Constants.MACHINETYPESEAL.equals(machineInfoTypeOnlineVO.getMachineType())) {
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())) {
                        machineInfoTypeCountVO.setSealOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                    } else {
                        machineInfoTypeCountVO.setSealOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                    }
                }
            }
        }
        return machineInfoTypeCountVO;
    }


}
