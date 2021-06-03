package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.common.enums.CurrencyEnum;
import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.AggMinerVO;
import com.mei.hui.miner.mapper.*;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.model.SysMinerInfoVO;
import com.mei.hui.miner.model.XchMinerDetailBO;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 矿工信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-03-02
 */
@Slf4j
@Service
public class SysMinerInfoServiceImpl implements ISysMinerInfoService
{
    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;
    @Autowired
    PoolInfoMapper poolInfoMapper;
    @Autowired
    private SysMachineInfoMapper sysMachineInfoMapper;
    @Autowired
    private ChiaMinerMapper chiaMinerMapper;
    @Autowired
    private SysAggPowerDailyMapper sysAggPowerDailyMapper;
    @Autowired
    private ISysAggPowerDailyService sysAggPowerDailyService;
    @Autowired
    private ISysAggAccountDailyService sysAggAccountDailyService;
    @Autowired
    private SysAggAccountDailyMapper sysAggAccountDailyMapper;
    /**
     * 查询矿工信息
     * @param id 主键
     * @return 矿工信息
     */
    @Override
    public XchMinerDetailBO getXchMinerById(Long id){
        log.info("获取起亚币,入参:minerId = {}",id);
        ChiaMiner xchMiner = chiaMinerMapper.selectById(id);
        log.info("获取起亚币,出参:{}", JSON.toJSONString(xchMiner));
        if (xchMiner == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"起亚币矿机不存在");
        }
        XchMinerDetailBO xchMinerDetailBO = new XchMinerDetailBO();
        xchMinerDetailBO.setBalanceMinerAccount(BigDecimalUtil.formatFour(xchMiner.getBalanceMinerAccount()));
        xchMinerDetailBO.setTotalBlocks(xchMiner.getTotalBlocks());
        xchMinerDetailBO.setPowerAvailable(xchMiner.getPowerAvailable());
        xchMinerDetailBO.setTotalBlockAward(BigDecimalUtil.formatFour(xchMiner.getTotalBlockAward()));

        /**
         * 获取算力增速，从算力聚合表查询前一天的算力增速
         */
        String date = DateUtils.getDate();
        String yesterDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.addDays(DateUtils.parseDate(date), -1));

        LambdaQueryWrapper<SysAggPowerDaily> query = new LambdaQueryWrapper<>();
        query.eq(SysAggPowerDaily::getMinerId,xchMiner.getMinerId());
        query.eq(SysAggPowerDaily::getType, CurrencyEnum.CHIA.name());
        query.eq(SysAggPowerDaily::getDate,yesterDateStr);
        List<SysAggPowerDaily> aggPowers = sysAggPowerDailyMapper.selectList(query);
        if (aggPowers.size() > 0){
            SysAggPowerDaily agg = aggPowers.get(0);
            xchMinerDetailBO.setPowerIncreasePerDay(xchMiner.getPowerAvailable().subtract(agg.getPowerAvailable()));
            xchMinerDetailBO.setBlocksPerDay(xchMiner.getTotalBlocks() - agg.getTotalBlocks());
        } else {
            xchMinerDetailBO.setPowerIncreasePerDay(BigDecimal.ZERO);
            xchMinerDetailBO.setBlocksPerDay(0L);
        }
        return xchMinerDetailBO;
    }

    /**
     * 查询矿工信息
     * @param id 矿工信息ID
     * @return 矿工信息
     */
    @Override
    public SysMinerInfo selectSysMinerInfoById(Long id)
    {
        SysMinerInfo miner = sysMinerInfoMapper.selectSysMinerInfoById(id);
        if (miner == null) {
            return null;
        }
        PoolInfo machine = poolInfoMapper.selectMachineInfoByUserIdAndMinerId(miner.getUserId(),miner.getMinerId());
        if (machine != null) {
            miner.setWorkerCount(machine.getWorkerCount());
        }
        miner.setSectorPledge(BigDecimalUtil.formatFour(miner.getSectorPledge()));
        miner.setLockAward(BigDecimalUtil.formatFour(miner.getLockAward()));
        miner.setTotalBlockAward(BigDecimalUtil.formatFour(miner.getTotalBlockAward()));
        miner.setBalanceMinerAvailable(BigDecimalUtil.formatFour(miner.getBalanceMinerAvailable()));
        miner.setBalanceMinerAccount(BigDecimalUtil.formatFour(miner.getBalanceMinerAccount()));
        miner.setPowerAvailable(BigDecimalUtil.formatTwo(miner.getPowerAvailable()));
        return miner;
    }

    /**
     * 通过miner_id查询矿工信息
     *
     * @param miner_id 矿工miner_id
     * @return 矿工信息
     */
    @Override
    public SysMinerInfo selectSysMinerInfoByMinerId(String miner_id) {
        return sysMinerInfoMapper.selectSysMinerInfoByMinerId(miner_id);
    }

    /**
     * 查询矿工信息列表
     * 
     * @param sysMinerInfo 矿工信息
     * @return 矿工信息
     */
    @Override
    public List<SysMinerInfo> selectSysMinerInfoList(SysMinerInfo sysMinerInfo){
        Long userId = HttpRequestUtil.getUserId();
        List<SysMinerInfo> list = new ArrayList<>();
        if(userId !=null && userId == 1L){
            log.info("查询矿工信息列表：【{}】",userId);
            list =  sysMinerInfoMapper.selectList(null);
        }else{
            LambdaQueryWrapper<SysMinerInfo> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(SysMinerInfo::getUserId,userId);
            list =  sysMinerInfoMapper.selectList(queryWrapper);
       }
        return list;
    }

    /**
     * 获取 起亚币 旷工列表
     * @return
     */
    @Override
    public List<SysMinerInfo> findXchMinerList(){
        Long userId = HttpRequestUtil.getUserId();
        List<ChiaMiner> list = null;
        if(userId !=null && userId == 1L){
            log.info("查询矿工信息列表：【{}】",userId);
            list = chiaMinerMapper.selectList(null);
        }else{
            LambdaQueryWrapper<ChiaMiner> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(ChiaMiner::getUserId,userId);
            list = chiaMinerMapper.selectList(queryWrapper);
        }
        List<SysMinerInfo> miners = list.stream().map(v -> {
            SysMinerInfo miner = new SysMinerInfo();
            miner.setUserId(userId);
            miner.setBalanceMinerAccount(v.getBalanceMinerAccount());
            miner.setMinerId(v.getMinerId());
            miner.setId(v.getId());
            return miner;
        }).collect(Collectors.toList());
        return miners;
    }


    public List<SysMinerInfo> findMinerInfoList(SysMinerInfo sysMinerInfo){
        return sysMinerInfoMapper.selectSysMinerInfoList(sysMinerInfo);
    }

    /**
     * 获取 fil 币旷工
     * @param sysMinerInfoBO
     * @return
     */
    @Override
    public Map<String,Object> findPage(SysMinerInfoBO sysMinerInfoBO)
    {
        boolean isAsc = sysMinerInfoBO.isAsc();
        String cloumName = sysMinerInfoBO.getCloumName();
        Long userId = HttpRequestUtil.getUserId();

        Page<SysMinerInfo> minerInfoPage = new Page<>(sysMinerInfoBO.getPageNum(),sysMinerInfoBO.getPageSize());
        IPage<SysMinerInfoVO> result = sysMinerInfoMapper.pageMinerInfo(minerInfoPage,userId,isAsc,cloumName);
        for (SysMinerInfoVO sysMinerInfoVO:result.getRecords()) {
            sysMinerInfoVO.setBalanceMinerAccount(BigDecimalUtil.formatFour(sysMinerInfoVO.getBalanceMinerAccount()));
            sysMinerInfoVO.setBalanceMinerAvailable(BigDecimalUtil.formatFour(sysMinerInfoVO.getBalanceMinerAvailable()));
            sysMinerInfoVO.setSectorPledge(BigDecimalUtil.formatFour(sysMinerInfoVO.getSectorPledge()));
            sysMinerInfoVO.setTotalBlockAward(BigDecimalUtil.formatFour(sysMinerInfoVO.getTotalBlockAward()));
            sysMinerInfoVO.setPowerAvailable(BigDecimalUtil.formatTwo(sysMinerInfoVO.getPowerAvailable()));
        }
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",minerInfoPage.getRecords());
        map.put("total",minerInfoPage.getTotal());
        return map;
    }

    public Long countByMinerId(String minerId) {
        return sysMinerInfoMapper.countByMinerId(minerId);
    }

    /**
     * 新增矿工信息
     * 
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    @Override
    public int insertSysMinerInfo(SysMinerInfo sysMinerInfo)
    {
        sysMinerInfo.setCreateTime(LocalDateTime.now());
        return sysMinerInfoMapper.insertSysMinerInfo(sysMinerInfo);
    }

    /**
     * 修改矿工信息
     * 
     * @param sysMinerInfo 矿工信息
     * @return 结果
     */
    @Override
    public int updateSysMinerInfo(SysMinerInfo sysMinerInfo)
    {
        sysMinerInfo.setUpdateTime(LocalDateTime.now());
        return sysMinerInfoMapper.updateSysMinerInfo(sysMinerInfo);
    }

    /**
     * 批量删除矿工信息
     * 
     * @param ids 需要删除的矿工信息ID
     * @return 结果
     */
    @Override
    public int deleteSysMinerInfoByIds(Long[] ids)
    {
        return sysMinerInfoMapper.deleteSysMinerInfoByIds(ids);
    }

    @Override
    public SysMinerInfo selectSysMinerInfoByUserIdAndMinerId(Long userId, String minerId) {
        return sysMinerInfoMapper.selectSysMinerInfoByUserIdAndMinerId(userId, minerId);
    }

    public Map<String,Object> machines(Long id,int pageNum,int pageSize) {

        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo miner = selectSysMinerInfoById(id);
        if (miner == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        if (userId != null && userId != 1L && !userId.equals(miner.getUserId())) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
        }

        LambdaQueryWrapper<SysMachineInfo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysMachineInfo::getMinerId,miner.getMinerId());
        IPage<SysMachineInfo> page = sysMachineInfoMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",page.getRecords());
        map.put("total",page.getTotal());
        return map;
    }
    /**
     * 通过userid 集合批量获取旷工
     */
    public Result<List<AggMinerVO>> findBatchMinerByUserId(List<Long> userIds) {
        if(userIds == null || userIds.size() == 0){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"用户集合不能为空");
        }
        List<AggMiner> list = sysMachineInfoMapper.findBatchMinerByUserId(userIds);
        List<AggMinerVO> lt = list.stream().map(v -> {
            AggMinerVO aggMinerVO = new AggMinerVO();
            BeanUtils.copyProperties(v,aggMinerVO);
            return aggMinerVO;
        }).collect(Collectors.toList());
        return Result.success(lt);
    }
    /**
     * 获取fil币聚合信息
     * @param id
     * @return
     */
    public Map<String,Object> dailyPower(Long id) {
        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo miner = selectSysMinerInfoById(id);
        if (miner == null) {
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        if (userId != null && 1L == userId && !userId.equals(miner.getUserId())) {
            MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
        }
        Date end = DateUtils.getNowDate();
        Date begin = DateUtils.addDays(end,-29);
        List<SysAggPowerDaily> list = sysAggPowerDailyService.selectSysAggAccountDailyByMinerId(miner.getMinerId(),  DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, begin), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, end),CurrencyEnum.FIL.name());
        list.stream().forEach(v->{
            // v.setPowerAvailable(v.getPowerIncrease());
            v.setPowerIncrease(v.getPowerAvailable());
        });
        Map<String,Object> map = new HashMap<>();
        map.put("code",ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",list);
        map.put("total",list.size());
        return map;
    }
    /**
     * 获取起亚币币聚合信息
     * @param id
     * @return
     */
    public Map<String,Object> chiaDailyPower(Long id) {
        Long userId = HttpRequestUtil.getUserId();
        log.info("获取起亚币,入参:minerId = {}",id);
        ChiaMiner xchMiner = chiaMinerMapper.selectById(id);
        log.info("获取起亚币,出参:{}", JSON.toJSONString(xchMiner));
        if (xchMiner == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"起亚币矿机不存在");
        }
        if (1L != userId && !userId.equals(xchMiner.getUserId())) {
            MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
        }
        Date end = DateUtils.getNowDate();
        Date begin = DateUtils.addDays(end,-29);
        LambdaQueryWrapper<SysAggPowerDaily> query = new LambdaQueryWrapper<>();
        query.eq(SysAggPowerDaily::getMinerId,xchMiner.getMinerId());
        query.gt(SysAggPowerDaily::getDate,DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, begin));
        query.lt(SysAggPowerDaily::getDate,DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, end));
        query.eq(SysAggPowerDaily::getType,CurrencyEnum.CHIA.name());
        List<SysAggPowerDaily> list =  sysAggPowerDailyMapper.selectList(query);
        list.stream().forEach(v->{
            // v.setPowerAvailable(v.getPowerIncrease());
            v.setPowerIncrease(v.getPowerAvailable());
        });
        Map<String,Object> map = new HashMap<>();
        map.put("code",ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",list);
        map.put("total",list.size());
        return map;
    }
    /**
     * 获取 fil 币 收益增长列表
     * @param id
     * @return
     */
    @Override
    public PageResult dailyAccount(Long id) {
        Long userId = HttpRequestUtil.getUserId();
        SysMinerInfo miner = selectSysMinerInfoById(id);
        if (miner == null) {
            throw new MyException(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        if (userId != 1L && !userId.equals(miner.getUserId())) {
            throw new MyException(MinerError.MYB_222222.getCode(),"没有权限");
        }
        Date end = DateUtils.getNowDate();
        Date begin = DateUtils.addDays(end,-29);
        List<SysAggAccountDaily> list = sysAggAccountDailyService.selectSysAggAccountDailyByMinerId(miner.getMinerId(),  DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, begin), DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, end),CurrencyEnum.FIL.name());

        list.stream().forEach(v->{
            v.setBalanceAccount(BigDecimalUtil.formatFour(v.getBalanceAccount()));
        });
        PageResult<SysAggAccountDaily> pageResult = new PageResult(list.size(), list);
        return pageResult;
    }
    /**
     * 获取 chia 币 收益增长列表
     * @param id
     * @return
     */
    @Override
    public PageResult chiaDailyAccount(Long id) {
        Long userId = HttpRequestUtil.getUserId();
        log.info("获取起亚币,入参:minerId = {}",id);
        ChiaMiner xchMiner = chiaMinerMapper.selectById(id);
        log.info("获取起亚币,出参:{}", JSON.toJSONString(xchMiner));
        if (xchMiner == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"起亚币矿机不存在");
        }
        if (1L != userId && !userId.equals(xchMiner.getUserId())) {
            MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
        }
        Date end = DateUtils.getNowDate();
        Date begin = DateUtils.addDays(end,-29);
        LambdaQueryWrapper<SysAggAccountDaily> query = new LambdaQueryWrapper<>();
        query.eq(SysAggAccountDaily::getMinerId,xchMiner.getMinerId());
        query.gt(SysAggAccountDaily::getDate,DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, begin));
        query.lt(SysAggAccountDaily::getDate,DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, end));
        query.eq(SysAggAccountDaily::getType,CurrencyEnum.CHIA.name());
        List<SysAggAccountDaily> list = sysAggAccountDailyMapper.selectList(query);
        list.stream().forEach(v->{
            v.setBalanceAccount(BigDecimalUtil.formatFour(v.getBalanceAccount()));
        });
        PageResult<SysAggAccountDaily> pageResult = new PageResult(list.size(), list);
        return pageResult;
    }


    @Override
    public Long selectFilAllBlocksPerDay() {
        return sysMinerInfoMapper.selectAllBlocksPerDay();
    }

    @Override
    public BigDecimal selectFilAllBalanceMinerAccount() {
        return sysMinerInfoMapper.selectAllBalanceMinerAccount();
    }

    @Override
    public BigDecimal selectFilAllPowerAvailable() {
        return sysMinerInfoMapper.selectAllPowerAvailable();
    }

    @Override
    public Long selectFilAllMinerIdCount() {
        return sysMinerInfoMapper.selectAllMinerIdCount();
    }


}
