package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.entity.SysSectorsWrap;
import com.mei.hui.miner.mapper.SysSectorsWrapMapper;
import com.mei.hui.miner.model.RequestSectorInfo;
import com.mei.hui.miner.service.ISysSectorInfoService;
import com.mei.hui.miner.service.ISysSectorsWrapService;
import com.mei.hui.util.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扇区信息聚合Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Slf4j
@Service
public class SysSectorsWrapServiceImpl implements ISysSectorsWrapService
{
    @Autowired
    private SysSectorsWrapMapper sysSectorsWrapMapper;

    @Autowired
    private ISysSectorInfoService sysSectorInfoService;

    /**
     * 查询扇区信息聚合
     * 
     * @param id 扇区信息聚合ID
     * @return 扇区信息聚合
     */
    @Override
    public SysSectorsWrap selectSysSectorsWrapById(Long id)
    {
        return sysSectorsWrapMapper.selectSysSectorsWrapById(id);
    }

    /**
     * 查询扇区信息聚合列表
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 扇区信息聚合
     */
    @Override
    public List<SysSectorsWrap> selectSysSectorsWrapList(SysSectorsWrap sysSectorsWrap)
    {
        return sysSectorsWrapMapper.selectSysSectorsWrapList(sysSectorsWrap);
    }

    /**
     * 新增扇区信息聚合
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    @Override
    public int insertSysSectorsWrap(SysSectorsWrap sysSectorsWrap)
    {
        sysSectorsWrap.setCreateTime(LocalDateTime.now());
        return sysSectorsWrapMapper.insertSysSectorsWrap(sysSectorsWrap);
    }

    /**
     * 修改扇区信息聚合
     * 
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    @Override
    public int updateSysSectorsWrap(SysSectorsWrap sysSectorsWrap)
    {
        sysSectorsWrap.setUpdateTime(LocalDateTime.now());
        return sysSectorsWrapMapper.updateSysSectorsWrap(sysSectorsWrap);
    }

    /**
     * 批量删除扇区信息聚合
     * 
     * @param ids 需要删除的扇区信息聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysSectorsWrapByIds(Long[] ids)
    {
        return sysSectorsWrapMapper.deleteSysSectorsWrapByIds(ids);
    }

    /**
     * 删除扇区信息聚合信息
     * 
     * @param id 扇区信息聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysSectorsWrapById(Long id)
    {
        return sysSectorsWrapMapper.deleteSysSectorsWrapById(id);
    }

    @Override
    public List<SysSectorsWrap> selectSysSectorsWrapListByUserId(SysSectorsWrap sysSectorsWrap, Long userId) {
        //sysSectorsWrap.getParams().put("userId", userId);
        userId = HttpRequestUtil.getUserId();
        return sysSectorsWrapMapper.selectSysSectorsWrapListByUserId(sysSectorsWrap);
    }

    @Override
    public Map<String,Object> list(SysSectorsWrap sysSectorsWrap) {
        Long userId = HttpRequestUtil.getUserId();
        sysSectorsWrap.getParams().put("userId", userId);
        PageHelper.startPage(Integer.valueOf(sysSectorsWrap.getPageNum()+""),Integer.valueOf(sysSectorsWrap.getPageSize()+""));
        List<SysSectorsWrap> list = sysSectorsWrapMapper.selectSysSectorsWrapListByUserId(sysSectorsWrap);
        /**
         * 组装返回信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("rows",list);
        map.put("total",new PageInfo(list).getTotal());
        return map;
    }

    /**
     * 查询该扇区聚合信息是否已存在
     *
     * @param sysSectorsWrap 扇区信息聚合
     * @return 结果
     */
    @Override
    public SysSectorsWrap selectSysSectorsWrapByMinerIdAndSectorNo(SysSectorsWrap sysSectorsWrap) {
        return sysSectorsWrapMapper.selectSysSectorsWrapByMinerIdAndSectorNo(sysSectorsWrap);
    }

    /**
    *
    * @description 新增扇区信息
    * @author shangbin
    * @date 2021/5/13 10:17
    * @param [sysSectorInfo]
    * @return int
    * @version v1.0.0
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addSector(RequestSectorInfo sysSectorInfo) {
        //1. 查询sys_sectors_wrap 中是否已有该扇区, 没有则插入, 有则获取数据做聚合
        SysSectorsWrap sysSectorsWrapParam = new SysSectorsWrap();
        sysSectorsWrapParam.setMinerId(sysSectorInfo.getMinerId()+"");
        sysSectorsWrapParam.setSectorNo(sysSectorInfo.getSectorNo());
        String hostname = sysSectorInfo.getHostname();
        if("none".equalsIgnoreCase(hostname)){
            hostname = "";
        }
        sysSectorsWrapParam.setHostname(hostname);
        sysSectorsWrapParam.setSectorDuration(sysSectorInfo.getSectorDuration());
        sysSectorsWrapParam.setSectorSize(sysSectorInfo.getSectorSize());
        sysSectorsWrapParam.setSectorStatus(sysSectorInfo.getSectorStatus());
        sysSectorsWrapParam.setCreateTime(LocalDateTime.now());
        sysSectorsWrapParam.setUpdateTime(LocalDateTime.now());

        SysSectorsWrap sysSectorsWrap = selectSysSectorsWrapByMinerIdAndSectorNo(sysSectorsWrapParam);
        if (sysSectorsWrap == null) {
        // TODO 一会修改，测试用
//        if (sysSectorsWrap != null) {
            try {
                log.info("新增扇区信息聚合表:[{}]" , JSON.toJSONString(sysSectorsWrapParam));
                insertSysSectorsWrap(sysSectorsWrapParam);
            }catch (DataIntegrityViolationException exception) {
                if (sysSectorsWrap.getSectorStatus() < sysSectorInfo.getSectorStatus()) {
                    log.info("新增扇区信息聚合表抛出异常，修改扇区信息聚合表:[{}]" , JSON.toJSONString(sysSectorsWrap));
                    updateSysSectorsWrapAddSector(sysSectorsWrap, sysSectorInfo);
                }
            }
        } else if (sysSectorsWrap.getSectorStatus() < sysSectorInfo.getSectorStatus()) {
            log.info("修改扇区信息聚合表:[{}]" , JSON.toJSONString(sysSectorsWrap));
            updateSysSectorsWrapAddSector(sysSectorsWrap, sysSectorInfo);
        }

        //2. 查询 sys_sector_info 中是否已存在该记录, 如果已存在则更新
        SysSectorInfo sectorInfo = sysSectorInfoService.selectSysSectorInfoByMinerIdAndSectorNoAndStatus(sysSectorInfo);
        int rows = 0;
        if (sectorInfo == null) {
//         TODO 一会修改 ，测试用
//        if (sectorInfo != null) {
            try {
                log.info("新增扇区信息表:[{}]" , JSON.toJSONString(sysSectorInfo));
                rows = sysSectorInfoService.insertSysSectorInfo(sysSectorInfo);
                return rows;
            }catch (DataIntegrityViolationException exception) {
                // 通过唯一性判断该条数据如果已经存在，则直接丢弃，不用更新，返回成功提示
                log.info("新增扇区信息表抛出异常：[{}]" , JSON.toJSONString(sysSectorInfo));
                return 1;
            }
        }
        sysSectorInfo.setId(sectorInfo.getId());
        log.info("修改扇区信息表:[{}]" , JSON.toJSONString(sysSectorInfo));
        rows = sysSectorInfoService.updateSysSectorInfo(sysSectorInfo);

        return rows;
    }

    /**
    *
    * @description 新增抛出异常后，修改扇区信息聚合表
    * @author shangbin
    * @date 2021/5/12 18:18
    * @param [sysSectorsWrap, sysSectorInfo]
    * @return int
    * @version v1.0.0
    */
    public int updateSysSectorsWrapAddSector(SysSectorsWrap sysSectorsWrap,RequestSectorInfo sysSectorInfo) {
        sysSectorsWrap.setSectorDuration(sysSectorsWrap.getSectorDuration() + sysSectorInfo.getSectorDuration());
        sysSectorsWrap.setSectorStatus(sysSectorInfo.getSectorStatus());
        return updateSysSectorsWrap(sysSectorsWrap);
    }

    @Override
    public int testInsert(SysSectorsWrap sysSectorsWrap) {
        return sysSectorsWrapMapper.insert(sysSectorsWrap);
    }
}
