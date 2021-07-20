package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.MinerLongitudeLatitude;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.entity.SysSectorsWrap;
import com.mei.hui.miner.mapper.SysSectorInfoMapper;
import com.mei.hui.miner.mapper.SysSectorsWrapMapper;
import com.mei.hui.miner.model.RequestSectorInfo;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.miner.service.ISysSectorInfoService;
import com.mei.hui.miner.service.ISysSectorsWrapService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 扇区信息聚合Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Slf4j
@Service
public class SysSectorsWrapServiceImpl extends ServiceImpl<SysSectorsWrapMapper,SysSectorsWrap> implements ISysSectorsWrapService
{
    @Autowired
    private SysSectorsWrapMapper sysSectorsWrapMapper;

    @Autowired
    private ISysSectorInfoService sysSectorInfoService;
    @Autowired
    private SysSectorInfoMapper sysSectorInfoMapper;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private RedisUtil redisUtil;

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

    /*@Override
    public List<SysSectorsWrap> selectSysSectorsWrapListByUserId(SysSectorsWrap sysSectorsWrap, Long userId) {
        //sysSectorsWrap.getParams().put("userId", userId);
        userId = HttpRequestUtil.getUserId();
        return sysSectorsWrapMapper.selectSysSectorsWrapListByUserId(sysSectorsWrap);
    }*/

    @Override
    public Map<String,Object> list(SysSectorsWrap sysSectorsWrap) {
        /**
         * 组装返回信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());

        Long userId = HttpRequestUtil.getUserId();
        QueryWrapper<SysMinerInfo> queryWrapper = new QueryWrapper<>();
        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        sysMinerInfo.setUserId(userId);
        queryWrapper.setEntity(sysMinerInfo);
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.list(queryWrapper);
        log.info("查询FIL币矿工信息表里该用户userId：【{}】所有的数据：【{}】",userId,JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList == null || sysMinerInfoList.size()<1){
            map.put("rows",new ArrayList<SysSectorsWrap>());
            map.put("total",0);
            return map;
        }
        List<String> minerIdList = sysMinerInfoList.stream().map(v->{
            return v.getMinerId();
        }).collect(Collectors.toList());
        log.info("该用户userId：【{}】所有的矿工为：【{}】",userId,JSON.toJSON(minerIdList));

        sysSectorsWrap.setMinerIdList(minerIdList);
        PageHelper.startPage(Integer.valueOf(sysSectorsWrap.getPageNum()+""),Integer.valueOf(sysSectorsWrap.getPageSize()+""));
        List<SysSectorsWrap> list = sysSectorsWrapMapper.selectSysSectorsWrapListByUserId(sysSectorsWrap);
        log.info("扇区信息表出参：【{}】",JSON.toJSON(list));
        for (SysSectorsWrap dbSysSectorsWrap:list) {
            // 显示GB，原来单位是B，结果肯定是整数，不会出来小数点
            dbSysSectorsWrap.setSectorSize(dbSysSectorsWrap.getSectorSize()/1024/1024/1024);
            if ("none".equals(dbSysSectorsWrap.getHostname())) {
                dbSysSectorsWrap.setHostname("");
            }
        }

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

    /**
     * 初始化扇区聚合表封装总时长
     */
    public void initSectorToRedis(){
        /**
         * 将扇区封装时长写入到缓存
         */
        int pageNum = 1;
        int pageSize = 500;
        while (true) {
            LambdaQueryWrapper<SysSectorInfo> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.lt(SysSectorInfo::getCreateTime,LocalDateTime.now());
            IPage<SysSectorInfo> page = sysSectorInfoMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
            page.getRecords().stream().forEach(v->{
                String minerId = v.getMinerId();
                Long sectorNo = v.getSectorNo();
                Integer sectorStatus = v.getSectorStatus();
                Long sectorDuration = v.getSectorDuration();
                //sector_{minerId}_{sectorNo}
                //sector_{state}
                String key = String.format("sector_%s_%s", minerId, sectorNo);
                String field = String.format("sector_state_%s", sectorStatus);
                redisUtil.hmset(key,field,sectorDuration+"",21600L);//有效期6个小时
                log.info("key={},field={},value={}",key,field,sectorDuration);
            });
            if (page.getRecords().size() < pageSize) {
                break;
            } else {
                pageNum ++;
            }
        }
        log.info("写入缓村完成");
    }

    public void initSectorDuration(){
        /**
         * 重新计算扇区封装总时长
         */
        int pageNum = 1;
        int pageSize = 500;
        while (true) {
            LambdaQueryWrapper<SysSectorsWrap> queryWrapper = new LambdaQueryWrapper();
            IPage<SysSectorsWrap> page = sysSectorsWrapMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);

            List<SysSectorsWrap> batch = page.getRecords().stream().map(v -> {
                BigDecimal duration = new BigDecimal("0");
                SysSectorsWrap wrap = new SysSectorsWrap();
                wrap.setId(v.getId());
                String key = String.format("sector_%s_%s", v.getMinerId(), v.getSectorNo());
                Map<String, String> map = redisUtil.hgetall(key);
                if (map != null && map.size() > 0) {
                    for (String k : map.keySet()) {
                        String value = map.get(k);
                        duration.add(new BigDecimal(value));
                    }
                }
                wrap.setSectorDuration(duration.longValue());
                return wrap;

            }).collect(Collectors.toList());

            if(batch.size() == 0){
                break;
            }

            this.updateBatchById(batch);

            if (page.getRecords().size() < pageSize) {
                break;
            } else {
                pageNum ++;
            }
        }
        log.info("重新计算扇区封装总时长,完成");
    }
}
