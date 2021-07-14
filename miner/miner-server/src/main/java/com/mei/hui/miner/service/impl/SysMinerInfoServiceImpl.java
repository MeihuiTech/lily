package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.*;
import com.mei.hui.miner.model.RequestMinerInfo;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.model.SysMinerInfoVO;
import com.mei.hui.miner.model.XchMinerDetailBO;
import com.mei.hui.miner.service.CurrencyRateService;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.FindSysUsersByNameBO;
import com.mei.hui.user.feign.vo.FindSysUsersByNameVO;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class SysMinerInfoServiceImpl extends ServiceImpl<SysMinerInfoMapper,SysMinerInfo> implements ISysMinerInfoService
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
    @Autowired
    private CurrencyRateService currencyRateService;
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private FilMinerControlBalanceMapper filMinerControlBalanceMapper;

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
        query.eq(SysAggPowerDaily::getType, CurrencyEnum.XCH.name());
        query.eq(SysAggPowerDaily::getDate,yesterDateStr);
        List<SysAggPowerDaily> aggPowers = sysAggPowerDailyMapper.selectList(query);
        log.info("算力按天聚合表出参：【{}】",JSON.toJSON(aggPowers));
        if (aggPowers.size() > 0){
            SysAggPowerDaily agg = aggPowers.get(0);
            xchMinerDetailBO.setPowerIncreasePerDay(xchMiner.getPowerAvailable().subtract(agg.getPowerAvailable()));
            xchMinerDetailBO.setBlocksPerDay(xchMiner.getTotalBlocks() - agg.getTotalBlocks());
        } else {
            xchMinerDetailBO.setPowerIncreasePerDay(xchMiner.getPowerAvailable());
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
        log.info("矿工信息：【{}】",JSON.toJSON(miner));
        if (miner == null) {
            return null;
        }
        PoolInfo machine = poolInfoMapper.selectMachineInfoByUserIdAndMinerId(miner.getUserId(),miner.getMinerId());
        log.info("矿机出参：【{}】",JSON.toJSON(machine));
        if (machine != null) {
            miner.setWorkerCount(machine.getWorkerCount());
        }
        // 查询FIL币算力按天聚合表里昨天所有的累计出块份数
        String yesterDayDate = DateUtils.getYesterDayDateYmd();
        Long yesterDayTotalBlocks = sysAggPowerDailyService.selectTotalBlocksByDate(yesterDayDate,CurrencyEnum.FIL.name(),miner.getMinerId());
        log.info("查询算力按天聚合表里昨天所有的累计出块份数出参：【{}】",yesterDayTotalBlocks);
        if (yesterDayTotalBlocks != null) {
            miner.setBlocksPerDay(miner.getTotalBlocks() - yesterDayTotalBlocks);
        } else {
            miner.setBlocksPerDay(miner.getTotalBlocks());
        }
        // 查询FIL币算力按天聚合表里昨天所有的有效算力
        BigDecimal yesterPowerIncrease = sysAggPowerDailyService.selectPowerIncreaseByDate(yesterDayDate,CurrencyEnum.FIL.name(),miner.getMinerId());
        log.info("查询FIL币算力按天聚合表里昨天所有的有效算力出参：【{}】",yesterPowerIncrease);
        if (yesterPowerIncrease != null) {
            miner.setPowerIncreasePerDay(miner.getPowerAvailable().subtract(yesterPowerIncrease));
        } else {
            miner.setPowerIncreasePerDay(miner.getPowerAvailable());
        }
        miner.setSectorPledge(BigDecimalUtil.formatFour(miner.getSectorPledge()));
        miner.setLockAward(BigDecimalUtil.formatFour(miner.getLockAward()));
        miner.setTotalBlockAward(BigDecimalUtil.formatFour(miner.getTotalBlockAward()));
        miner.setBalanceMinerAvailable(BigDecimalUtil.formatFour(miner.getBalanceMinerAvailable()));
        miner.setBalanceMinerAccount(BigDecimalUtil.formatFour(miner.getBalanceMinerAccount()));
        miner.setPowerAvailable(BigDecimalUtil.formatTwo(miner.getPowerAvailable()));
        miner.setBalanceWorkerAccount(BigDecimalUtil.formatFour(miner.getBalanceWorkerAccount()));

        // Post账户余额
        QueryWrapper<FilMinerControlBalance> queryWrapper = new QueryWrapper<>();
        FilMinerControlBalance filMinerControlBalance = new FilMinerControlBalance();
        filMinerControlBalance.setMinerId(miner.getMinerId());
        filMinerControlBalance.setName("control-0");
        queryWrapper.setEntity(filMinerControlBalance);
        List<FilMinerControlBalance> filMinerControlBalanceList = filMinerControlBalanceMapper.selectList(queryWrapper);
        log.info("Post账户余额表出参：【{}】",JSON.toJSON(filMinerControlBalanceList));
        if (filMinerControlBalanceList != null && filMinerControlBalanceList.size() > 0) {
            miner.setPostBalance(BigDecimalUtil.formatFour(filMinerControlBalanceList.get(0).getBalance()));
        } else {
            miner.setPostBalance(BigDecimal.ZERO);
        }

        // 矿机数量
        List<MachineInfoTypeOnlineVO> machineInfoTypeOnlineVOList = sysMachineInfoMapper.selectMachineInfoTypeOnlineCountList(miner.getMinerId());
        log.info("按照机器类型、是否在线分组查询矿机信息表的数量出参：【{}】",JSON.toJSON(machineInfoTypeOnlineVOList));

        // 赋默认值
        miner.setAllOnlineMachineCount(0);
        miner.setAllOfflineMachineCount(0);
        miner.setMinerOnlineMachineCount(0);
        miner.setMinerOfflineMachineCount(0);
        miner.setPostOnlineMachineCount(0);
        miner.setPostOfflineMachineCount(0);
        miner.setCtwoOnlineMachineCount(0);
        miner.setCtwoOfflineMachineCount(0);
        miner.setSealOnlineMachineCount(0);
        miner.setSealOfflineMachineCount(0);

        if (machineInfoTypeOnlineVOList != null && machineInfoTypeOnlineVOList.size() > 0){
            Integer allOnlineMachineCount = 0;
            Integer allOfflineMachineCount = 0;
            for (MachineInfoTypeOnlineVO machineInfoTypeOnlineVO : machineInfoTypeOnlineVOList){
                log.info("按照机器类型、是否在线分组查询矿机信息表的数量：【{}】",machineInfoTypeOnlineVO);
                if (Constants.MACHINETYPEMINER.equals(machineInfoTypeOnlineVO.getMachineType())){
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())){
                        miner.setMinerOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOfflineMachineCount += machineInfoTypeOnlineVO.getCount();
                    } else {
                        miner.setMinerOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOnlineMachineCount += machineInfoTypeOnlineVO.getCount();
                    }
                } else if (Constants.MACHINETYPEPOST.equals(machineInfoTypeOnlineVO.getMachineType())){
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())){
                        miner.setPostOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOfflineMachineCount += machineInfoTypeOnlineVO.getCount();
                    } else {
                        miner.setPostOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOnlineMachineCount += machineInfoTypeOnlineVO.getCount();
                    }
                } else if (Constants.MACHINETYPECTWO.equals(machineInfoTypeOnlineVO.getMachineType())){
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())){
                        miner.setCtwoOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOfflineMachineCount += machineInfoTypeOnlineVO.getCount();
                    } else {
                        miner.setCtwoOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOnlineMachineCount += machineInfoTypeOnlineVO.getCount();
                    }
                } else if (Constants.MACHINETYPESEAL.equals(machineInfoTypeOnlineVO.getMachineType())){
                    if (Constants.MACHINEONLINEZERO.equals(machineInfoTypeOnlineVO.getOnline())){
                        miner.setSealOfflineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOfflineMachineCount += machineInfoTypeOnlineVO.getCount();
                    } else {
                        miner.setSealOnlineMachineCount(machineInfoTypeOnlineVO.getCount());
                        allOnlineMachineCount += machineInfoTypeOnlineVO.getCount();
                    }
                }
            }
            miner.setAllOnlineMachineCount(allOnlineMachineCount);
            miner.setAllOfflineMachineCount(allOfflineMachineCount);
        }

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
     * 获取 起亚币 矿工列表
     * @return
     */
    @Override
    public List<SysMinerInfo> findXchMinerList(){
        Long userId = HttpRequestUtil.getUserId();
        List<ChiaMiner> list = null;
        if(userId !=null && userId == 1L){
            log.info("查询矿工信息列表入参：【{}】",userId);
            list = chiaMinerMapper.selectList(null);
        }else{
            LambdaQueryWrapper<ChiaMiner> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(ChiaMiner::getUserId,userId);
            list = chiaMinerMapper.selectList(queryWrapper);
        }
        log.info("查询矿工信息列表出参：【{}】",JSON.toJSON(list));
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

    @Override
    public List<SysMinerInfo> findMinerInfoList(SysMinerInfo sysMinerInfo){
        return sysMinerInfoMapper.selectSysMinerInfoList(sysMinerInfo);
    }

    /**
     * 查询fil矿工信息列表
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
        log.info("分页查询矿工表出参：【{}】",JSON.toJSON(result));
        for (SysMinerInfoVO sysMinerInfoVO:result.getRecords()) {
            // 查询FIL币算力按天聚合表里昨天所有的累计出块份数
            String yesterDayDate = DateUtils.getYesterDayDateYmd();
            Long yesterDayTotalBlocks = sysAggPowerDailyService.selectTotalBlocksByDate(yesterDayDate,CurrencyEnum.FIL.name(),sysMinerInfoVO.getMinerId());
            log.info("查询算力按天聚合表里昨天所有的累计出块份数出参：【{}】",yesterDayTotalBlocks);
            if (yesterDayTotalBlocks != null) {
                sysMinerInfoVO.setBlocksPerDay(sysMinerInfoVO.getTotalBlocks() - yesterDayTotalBlocks);
            } else {
                sysMinerInfoVO.setBlocksPerDay(sysMinerInfoVO.getTotalBlocks());
            }
            // 查询FIL币算力按天聚合表里昨天所有的有效算力
            BigDecimal yesterPowerIncrease = sysAggPowerDailyService.selectPowerIncreaseByDate(yesterDayDate,CurrencyEnum.FIL.name(),sysMinerInfoVO.getMinerId());
            log.info("查询FIL币算力按天聚合表里昨天所有的有效算力出参：【{}】",yesterPowerIncrease);
            if (yesterPowerIncrease != null) {
                sysMinerInfoVO.setPowerIncreasePerDay(sysMinerInfoVO.getPowerAvailable().subtract(yesterPowerIncrease));
            } else {
                sysMinerInfoVO.setPowerIncreasePerDay(sysMinerInfoVO.getPowerAvailable());
            }
            sysMinerInfoVO.setBalanceMinerAccount(BigDecimalUtil.formatFour(sysMinerInfoVO.getBalanceMinerAccount()));
            sysMinerInfoVO.setBalanceMinerAvailable(BigDecimalUtil.formatFour(sysMinerInfoVO.getBalanceMinerAvailable()));
            sysMinerInfoVO.setSectorPledge(BigDecimalUtil.formatFour(sysMinerInfoVO.getSectorPledge()));
            sysMinerInfoVO.setTotalBlockAward(BigDecimalUtil.formatFour(sysMinerInfoVO.getTotalBlockAward()));
            sysMinerInfoVO.setPowerAvailable(BigDecimalUtil.formatTwo(sysMinerInfoVO.getPowerAvailable()));
            sysMinerInfoVO.setBalanceWorkerAccount(BigDecimalUtil.formatFour(sysMinerInfoVO.getBalanceWorkerAccount()));

            // PoSt账户余额
            QueryWrapper<FilMinerControlBalance> queryWrapper = new QueryWrapper<>();
            FilMinerControlBalance filMinerControlBalance = new FilMinerControlBalance();
            filMinerControlBalance.setMinerId(sysMinerInfoVO.getMinerId());
            filMinerControlBalance.setName("control-0");
            queryWrapper.setEntity(filMinerControlBalance);
            List<FilMinerControlBalance> filMinerControlBalanceList = filMinerControlBalanceMapper.selectList(queryWrapper);
            log.info("PoSt账户余额表出参：【{}】",JSON.toJSON(filMinerControlBalanceList));
            if (filMinerControlBalanceList != null && filMinerControlBalanceList.size() > 0) {
                sysMinerInfoVO.setPostBalance(BigDecimalUtil.formatFour(filMinerControlBalanceList.get(0).getBalance()));
            } else {
                sysMinerInfoVO.setPostBalance(BigDecimal.ZERO);
            }

            // 矿机数量
            List<OnlineMachineCountVO> onlineMachineCountVOList = sysMachineInfoMapper.selectOnlineMachineCountVO(sysMinerInfoVO.getMinerId());
            if(onlineMachineCountVOList == null || onlineMachineCountVOList.size() < 1){
                sysMinerInfoVO.setOnlineMachineCount(0L);
                sysMinerInfoVO.setOffMachineCount(0L);
            }
            for (OnlineMachineCountVO onlineMachineCountVO : onlineMachineCountVOList) {
                if (onlineMachineCountVO.getOnline() == 1){
                    sysMinerInfoVO.setOnlineMachineCount(onlineMachineCountVO.getCount());
                } else {
                    sysMinerInfoVO.setOffMachineCount(onlineMachineCountVO.getCount());
                }
            }

        }
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",minerInfoPage.getRecords());
        map.put("total",minerInfoPage.getTotal());
        return map;
    }

    @Override
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

    @Override
    public Map<String,Object> machines(Long id,int pageNum,int pageSize,Integer online,String machineType) {

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
        if(online != null){
            queryWrapper.eq(SysMachineInfo::getOnline,online);
        }
        if(StringUtils.isNotEmpty(machineType)){
            queryWrapper.eq(SysMachineInfo::getMachineType,machineType);
        }
        queryWrapper.orderByDesc(SysMachineInfo::getUpdateTime);
        IPage<SysMachineInfo> page = sysMachineInfoMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",page.getRecords());
        map.put("total",page.getTotal());
        return map;
    }

    /**
     * 获取fil币聚合信息
     * @param id
     * @return
     */
    @Override
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
    @Override
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
        query.eq(SysAggPowerDaily::getType,CurrencyEnum.XCH.name());
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
        query.eq(SysAggAccountDaily::getType,CurrencyEnum.XCH.name());
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

    /**
     * 根据userId查询fil币矿工信息表里的该用户所有的矿工ID
     * @param userId
     * @return
     */
    @Override
    public List<String> findMinerIdByUserId(Long userId) {
        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        sysMinerInfo.setUserId(userId);
        QueryWrapper<SysMinerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(sysMinerInfo);
        List<SysMinerInfo> chiaMinerList = sysMinerInfoMapper.selectList(queryWrapper);
        List<String> minerIdList = chiaMinerList.stream().map(v -> {
            return v.getMinerId();
        }).collect(Collectors.toList());
        return minerIdList;
    }

    /*管理员-用户收益-分页查询用户收益列表*/
    @Override
    public PageResult<FilUserMoneyVO> selectUserMoneyList(FilUserMoneyBO filUserMoneyBO) {
        //用于入参模块模糊查询，获取用户id的list
        String userName = filUserMoneyBO.getUserName();
        List<Long> userIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(userName)) {
            FindSysUsersByNameBO bo = new FindSysUsersByNameBO();
            bo.setName(userName);
            log.info("模糊查询用户id集合");
            Result<List<FindSysUsersByNameVO>> userResult = userFeignClient.findSysUsersByName(bo);
            log.info("模糊查询用户id集合结果:{}", JSON.toJSONString(userResult));
            List<Long> idList = new ArrayList<>();
            if(ErrorCode.MYB_000000.getCode().equals(userResult.getCode()) && userResult.getData().size() > 0){
                idList = userResult.getData().stream().map(v ->v.getUserId()).collect(Collectors.toList());
                // id去重
                Set<Long> idsSet = new HashSet<>(idList);
                userIdList = new ArrayList<Long>(idsSet);
            } else {
                return new PageResult(0,new ArrayList());
            }
        }

        Page<FilUserMoneyVO> page = new Page<FilUserMoneyVO>(filUserMoneyBO.getPageNum(),filUserMoneyBO.getPageSize());
        log.info("多条件分页查询用户收益列表入参page：【{}】,filUserMoneyBO：【{}】,userIdList：【{}】",JSON.toJSON(page),JSON.toJSON(filUserMoneyBO),userIdList);
        IPage<FilUserMoneyVO> result = sysMinerInfoMapper.selectUserMoneyList(page,filUserMoneyBO.getUserId(),filUserMoneyBO.getCloumName(),filUserMoneyBO.isAsc(),userIdList);
        log.info("多条件分页查询用户收益列表出参：【{}】",JSON.toJSON(result));
        if (result == null){
            return new PageResult(0,new ArrayList());
        }
        List<Long> dbUserIdList = result.getRecords().stream().map(v -> {
            return v.getUserId();
        }).collect(Collectors.toList());
        Map<Long,BigDecimal> rateMap = currencyRateService.getUserIdRateMapByUserIdList(dbUserIdList,"FIL");

        result.getRecords().stream().forEach(v -> {
            v.setPowerAvailable(BigDecimalUtil.formatTwo(v.getPowerAvailable()));
            v.setTotalBlockAward(BigDecimalUtil.formatFour(v.getTotalBlockAward()));
            v.setFeeRate(rateMap.get(v.getUserId()));

            SysUserOut sysUserOut = new SysUserOut();
            sysUserOut.setUserId(v.getUserId());
            log.info("查询用户姓名入参：【{}】",JSON.toJSON(sysUserOut));
            Result<SysUserOut> sysUserOutResult = userFeignClient.getUserById(sysUserOut);
            log.info("查询用户姓名出参：【{}】",JSON.toJSON(sysUserOutResult));
            if(ErrorCode.MYB_000000.getCode().equals(sysUserOutResult.getCode())){
                v.setUserName(sysUserOutResult.getData().getUserName());
            }
        });
        PageResult pageResult = new PageResult(result.getTotal(),result.getRecords());
        return pageResult;
    }

    /*新增矿工上报接口*/
    @Override
    public int insertReportedSysMinerInfo(Long userId, RequestMinerInfo sysMinerInfo) {
        SysMinerInfo miner  = selectSysMinerInfoByUserIdAndMinerId(userId, sysMinerInfo.getMinerId());
        int rows = 0;
        if (miner == null) {
            log.info("新增矿工上报接口:【{}】",JSON.toJSON(sysMinerInfo));
            rows = insertSysMinerInfo(sysMinerInfo);
        } else {
            sysMinerInfo.setId(miner.getId());
            log.info("新增矿工上报已存在，更新矿工几款:【{}】",JSON.toJSON(sysMinerInfo));
            rows = updateSysMinerInfo(sysMinerInfo);
        }

        List<FilMinerControlBalance> filMinerControlBalanceList = sysMinerInfo.getControlAccounts();
        if(filMinerControlBalanceList != null && filMinerControlBalanceList.size() > 0) {
            String minerId = sysMinerInfo.getMinerId();
            for (FilMinerControlBalance filMinerControlBalance:filMinerControlBalanceList) {
                QueryWrapper<FilMinerControlBalance> queryWrapper = new QueryWrapper<>();
                FilMinerControlBalance selectFilMinerControlBalance = new FilMinerControlBalance();
                selectFilMinerControlBalance.setMinerId(minerId);
                selectFilMinerControlBalance.setName(filMinerControlBalance.getName());
                queryWrapper.setEntity(selectFilMinerControlBalance);
                List<FilMinerControlBalance> dbFilMinerControlBalanceList = filMinerControlBalanceMapper.selectList(queryWrapper);
                log.info("查询FilMinerControlBalance是否存在出参：【{}】",JSON.toJSON(dbFilMinerControlBalanceList));
                filMinerControlBalance.setMinerId(minerId);
                if (dbFilMinerControlBalanceList == null || dbFilMinerControlBalanceList.size() < 1) {
                    filMinerControlBalance.setCreateTime(LocalDateTime.now());
                    log.info("新增FilMinerControlBalance入参：【{}】",JSON.toJSON(filMinerControlBalance));
                    filMinerControlBalanceMapper.insert(filMinerControlBalance);
                } else {
                    filMinerControlBalance.setId(dbFilMinerControlBalanceList.get(0).getId());
                    log.info("修改FilMinerControlBalance入参：【{}】",JSON.toJSON(filMinerControlBalance));
                    filMinerControlBalanceMapper.updateById(filMinerControlBalance);
                }
            }
        }
        return rows;
    }

    /*对外API-矿工数据*/
    @Override
    public Result<ForeignSysMinerInfoVO> selectForeignMiner(ForeignMinerBO foreignMinerBO) {
        String minerId = foreignMinerBO.getMinerId();
        QueryWrapper<SysMinerInfo> queryWrapper = new QueryWrapper<>();
        SysMinerInfo selectSysMinerInfo = new SysMinerInfo();
        selectSysMinerInfo.setMinerId(minerId);
        queryWrapper.setEntity(selectSysMinerInfo);
        List<SysMinerInfo> dbSysMinerInfoList = sysMinerInfoMapper.selectList(queryWrapper);
        log.info("dbSysMinerInfoList出参：【{}】", JSON.toJSON(dbSysMinerInfoList));
        if (dbSysMinerInfoList == null || dbSysMinerInfoList.size() < 1) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工不存在");
        }

        SysMinerInfo sysMinerInfo = selectSysMinerInfoById(dbSysMinerInfoList.get(0).getId());
        log.info("查询矿工信息出参：【{}】",JSON.toJSON(sysMinerInfo));
        if (sysMinerInfo == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工不存在");
        }
        ForeignSysMinerInfoVO foreignSysMinerInfoVO = new ForeignSysMinerInfoVO();
        BeanUtils.copyProperties(sysMinerInfo,foreignSysMinerInfoVO);
        log.info("foreignSysMinerInfoVO:【{}】",JSON.toJSON(foreignSysMinerInfoVO));
        return Result.success(foreignSysMinerInfoVO);
    }

    /*对外API-用户数据*/
    @Override
    public Result<ForeignSysMinerInfoVO> selectForeignUser(ForeignUserBO foreignUserBO) {
        String apiKey = foreignUserBO.getApiKey();
        Result<Long> userIdResult = userFeignClient.findUserIdByApiKey(apiKey);
        log.info("根据apiKey查询用户的userId出参：【{}】",JSON.toJSON(userIdResult));
        if (!ErrorCode.MYB_000000.getCode().equals(userIdResult.getCode())
                || userIdResult.getData() == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"apiKey不存在");
        }
        Long userId = userIdResult.getData();

        QueryWrapper<SysMinerInfo> queryWrapper = new QueryWrapper<>();
        SysMinerInfo selectSysMinerInfo = new SysMinerInfo();
        selectSysMinerInfo.setUserId(userId);
        queryWrapper.setEntity(selectSysMinerInfo);
        List<SysMinerInfo> dbSysMinerInfoList = sysMinerInfoMapper.selectList(queryWrapper);
        log.info("dbSysMinerInfoList出参：【{}】",JSON.toJSON(dbSysMinerInfoList));
        if (dbSysMinerInfoList == null || dbSysMinerInfoList.size() < 1) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工不存在");
        }

        List<ForeignSysMinerInfoVO> foreignSysMinerInfoVOList = new ArrayList<>();
        for (SysMinerInfo dbSysMinerInfo : dbSysMinerInfoList) {
            SysMinerInfo sysMinerInfo = selectSysMinerInfoById(dbSysMinerInfo.getId());
            log.info("查询矿工信息出参：【{}】",JSON.toJSON(sysMinerInfo));
            if (sysMinerInfo == null) {
                throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工不存在");
            }
            ForeignSysMinerInfoVO foreignSysMinerInfoVO = new ForeignSysMinerInfoVO();
            BeanUtils.copyProperties(sysMinerInfo,foreignSysMinerInfoVO);
            log.info("foreignSysMinerInfoVO:【{}】",JSON.toJSON(foreignSysMinerInfoVO));
            foreignSysMinerInfoVOList.add(foreignSysMinerInfoVO);
        }

        return Result.success(foreignSysMinerInfoVOList);
    }

    /*不分页根据FIL币矿工信息表entity查询FIL币矿工信息表list*/
    @Override
    public List<SysMinerInfo> selectSysMinerInfoListBySysMinerInfo(SysMinerInfo sysMinerInfo) {
        QueryWrapper<SysMinerInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(sysMinerInfo);
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoMapper.selectList(queryWrapper);
        return sysMinerInfoList;
    }




}
