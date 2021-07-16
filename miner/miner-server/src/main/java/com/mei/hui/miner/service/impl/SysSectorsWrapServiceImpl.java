package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.Constants;
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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
        for (SysSectorsWrap dbSysSectorsWrap:list) {
            // 显示GB，原来单位是B，结果肯定是整数，不会出来小数点
            dbSysSectorsWrap.setSectorSize(dbSysSectorsWrap.getSectorSize()/1024/1024/1024);
            if ("none".equals(dbSysSectorsWrap.getHostname())) {
                dbSysSectorsWrap.setHostname("");
            }
        }
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
    * 新增扇区信息
     * 读数据库未提交的数据
     *
    * @description
    * @author shangbin
    * @date 2021/5/13 10:17
    * @return int
    * @version v1.0.0
    */
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public int addSector(RequestSectorInfo sysSectorInfo) {
        SysSectorsWrap sysSectorsWrapParam = new SysSectorsWrap();
        sysSectorsWrapParam.setMinerId(sysSectorInfo.getMinerId()+"");
        sysSectorsWrapParam.setSectorNo(sysSectorInfo.getSectorNo());
        String hostname = sysSectorInfo.getHostname();
        /*if("none".equalsIgnoreCase(hostname)){
            hostname = "";
        }*/
        sysSectorsWrapParam.setHostname(hostname);
//        sysSectorsWrapParam.setSectorDuration(sysSectorInfo.getSectorDuration());
        sysSectorsWrapParam.setSectorSize(sysSectorInfo.getSectorSize());
        sysSectorsWrapParam.setSectorStatus(sysSectorInfo.getSectorStatus());
        sysSectorsWrapParam.setCreateTime(LocalDateTime.now());
        sysSectorsWrapParam.setUpdateTime(LocalDateTime.now());

        SysSectorInfo sectorInfo = sysSectorInfoService.selectSysSectorInfoByMinerIdAndSectorNoAndStatus(sysSectorInfo);
        log.info("查询扇区封装记录表 sys_sector_info里的扇区信息是否已存在出参：【{}】",JSON.toJSON(sysSectorInfo));

        // 查询 扇区封装记录表sys_sector_info 中是否已存在该记录, 不存在插入，如果已存在则更新
        if (Constants.ACTIONSTART.equals(sysSectorInfo.getAction())){
            sysSectorInfo.setSectorStart(sysSectorInfo.getTime());
            sysSectorInfo.setSectorDuration(0L);
            sysSectorInfo.setStatus(0);
            log.info("先传过来的开始，封装时间设置为0,sysSectorInfo为：【{}】",JSON.toJSON(sysSectorInfo));
        } else if (Constants.ACTIONSTOP.equals(sysSectorInfo.getAction())) {
            sysSectorInfo.setSectorEnd(sysSectorInfo.getTime());
            sysSectorInfo.setStatus(1);
            if(sectorInfo != null && sectorInfo.getSectorStart() != null){
                sysSectorInfo.setSectorDuration(Duration.between(sectorInfo.getSectorStart(),sysSectorInfo.getTime()).toMillis()/1000);
                sysSectorsWrapParam.setSectorDuration(Duration.between(sectorInfo.getSectorStart(),sysSectorInfo.getTime()).toMillis()/1000);
                log.info("后传过来的结束，sysSectorInfo为：【{}】",JSON.toJSON(sysSectorInfo));
            } else {
                // 异常情况下，先传过来的结束，封装时间设置为0
                sysSectorInfo.setSectorDuration(0L);
                sysSectorsWrapParam.setSectorDuration(0L);
                log.info("异常情况下，先传过来的结束，封装时间设置为0,sysSectorInfo为：【{}】",JSON.toJSON(sysSectorInfo));
            }
        }
        if (sectorInfo == null) {
            try {
                log.info("新增扇区信息表:[{}]" , JSON.toJSONString(sysSectorInfo));
                sysSectorInfoService.insertSysSectorInfo(sysSectorInfo);
            }catch (DataIntegrityViolationException exception) {
                // 通过唯一性判断该条数据如果已经存在，则直接丢弃，不用更新，返回成功提示
                log.info("新增扇区信息表抛出异常：[{}]" , JSON.toJSONString(sysSectorInfo));
            }
        } else {
            sysSectorInfo.setId(sectorInfo.getId());
            log.info("修改扇区信息表:[{}]" , JSON.toJSONString(sysSectorInfo));
            sysSectorInfoService.updateSysSectorInfo(sysSectorInfo);
        }

        // 查询数据库里该miner_id、sector_no小于传过来的sector_status的值是否有进行中的状态，如果有，改成已完成
        List<SysSectorInfo> dbSysSectorInfoList = sysSectorInfoService.selectSysSectorInfoByMinerIdAndSectorNoAndSectorAndLtStatus(sysSectorInfo);
        log.info("查询数据库里该miner_id、sector_no小于传过来的sector_status的值是否有进行中的状态出参：【{}】",JSON.toJSON(dbSysSectorInfoList));
        if (dbSysSectorInfoList != null && dbSysSectorInfoList.size() > 0) {
            for (SysSectorInfo dbSysSectorInfo:dbSysSectorInfoList){
                dbSysSectorInfo.setStatus(1);
                sysSectorInfoService.updateSysSectorInfo(dbSysSectorInfo);
            }
        }

        // 如果是扇区开始封装，不插入/更新sys_sectors_wrap表，只有是扇区结束封装，才插入/更新sys_sectors_wrap表
        if (Constants.ACTIONSTART.equals(sysSectorInfo.getAction())){
            return 1;
        }

        int rows = 0;
        // 查询该 扇区聚合信息表sys_sectors_wrap 是否已有该扇区, 没有则插入, 有则获取数据做聚合
        log.info("查询该扇区聚合信息是否已存在入参：【{}】",JSON.toJSON(sysSectorsWrapParam));
        SysSectorsWrap sysSectorsWrap = selectSysSectorsWrapByMinerIdAndSectorNo(sysSectorsWrapParam);
        log.info("查询该扇区聚合信息是否已存在出参：【{}】",JSON.toJSON(sysSectorsWrap));
        if (sysSectorsWrap == null) {
            try {
                log.info("新增扇区信息聚合表入参:[{}]" , JSON.toJSONString(sysSectorsWrapParam));
                rows = insertSysSectorsWrap(sysSectorsWrapParam);
            }catch (DataIntegrityViolationException exception) {
                if (sysSectorInfo != null && sysSectorsWrap != null && sysSectorsWrap.getSectorStatus() < sysSectorInfo.getSectorStatus()) {
                    log.info("新增扇区信息聚合表抛出异常，修改扇区信息聚合表sysSectorsWrap:【{}】,sysSectorInfo:【{}】,sectorInfo:【{}】" ,
                            JSON.toJSONString(sysSectorsWrap),JSON.toJSON(sysSectorInfo),JSON.toJSON(sectorInfo));
                    rows = updateSysSectorsWrapAddSector(sysSectorsWrap, sysSectorInfo, sectorInfo);
                }
                log.info("新增扇区信息聚合表抛出异常");
            }
        } else {
            // 状态不是按照顺序来的，可能先来2，再来1
            log.info("修改扇区信息聚合表sysSectorsWrap:【{}】,sysSectorInfo:【{}】,sectorInfo:【{}】" ,
                    JSON.toJSONString(sysSectorsWrap),JSON.toJSON(sysSectorInfo),JSON.toJSON(sectorInfo));
            rows = updateSysSectorsWrapAddSector(sysSectorsWrap, sysSectorInfo, sectorInfo);
        }

        return rows;
    }

    /**
    *
    * @description 新增抛出异常后，修改扇区信息聚合表
    * @author shangbin
    * @date 2021/5/12 18:18
    * @param sysSectorsWrap 数据库里查出来的
    * @param sysSectorInfo 前端传过来的
     * @param sectorInfo 数据库里查出来的
    * @return int
    * @version v1.0.0
    */
    public int updateSysSectorsWrapAddSector(SysSectorsWrap sysSectorsWrap,RequestSectorInfo sysSectorInfo,SysSectorInfo sectorInfo) {
        if(sectorInfo == null) {
            log.info("updateSysSectorsWrapAddSector修改扇区信息聚合表sysSectorsWrap.getSectorDuration():【{}】,sysSectorInfo.getSectorDuration():【{}】",
                    sysSectorsWrap.getSectorDuration(),sysSectorInfo.getSectorDuration());
            sysSectorsWrap.setSectorDuration(sysSectorsWrap.getSectorDuration() + sysSectorInfo.getSectorDuration());
        } else {
            log.info("updateSysSectorsWrapAddSector修改扇区信息聚合表sysSectorsWrap.getSectorDuration():【{}】,sectorInfo.getSectorDuration():【{}】,sysSectorInfo.getSectorDuration():【{}】",
                    sysSectorsWrap.getSectorDuration(),sectorInfo.getSectorDuration(),sysSectorInfo.getSectorDuration());
            sysSectorsWrap.setSectorDuration(sysSectorsWrap.getSectorDuration() - sectorInfo.getSectorDuration() + sysSectorInfo.getSectorDuration());
        }
        sysSectorsWrap.setSectorStatus(sysSectorInfo.getSectorStatus());
        return updateSysSectorsWrap(sysSectorsWrap);
    }

    @Override
    public int testInsert(SysSectorsWrap sysSectorsWrap) {
        return sysSectorsWrapMapper.insert(sysSectorsWrap);
    }
}
