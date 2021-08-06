package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilMinerControlBalance;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBillMapper;
import com.mei.hui.miner.mapper.FilMinerControlBalanceMapper;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.MyException;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * FIL币账单 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-21
 */
@Service
@Slf4j
public class FilBillServiceImpl extends ServiceImpl<FilBillMapper, FilBill> implements FilBillService {
    @Autowired
    private FilBillMapper filBillMapper;
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private FilMinerControlBalanceMapper filMinerControlBalanceMapper;


    /*账单方法下拉列表*/
    @Override
    public List<String> selectFilBillMethodList(FilBillMethodBO filBillMethodBO) {
        String monthDate = filBillMethodBO.getMonthDate();
        String startDate = monthDate + "-01 00:00:00";
        String endDate = (DateUtils.getAssignEndDayOfMonth(Integer.valueOf(monthDate.substring(0,4)),Integer.valueOf(monthDate.substring(5,7))) + "").substring(0,19);
        return filBillMapper.selectFilBillMethodList(filBillMethodBO.getMinerId(),filBillMethodBO.getSubAccount(),startDate,endDate);
    }

    /*矿工子账户下拉列表*/
    @Override
    public List<FilBillSubAccountVO> selectFilBillSubAccountList(FilBillMethodBO filBillMethodBO) {
        String minerId = filBillMethodBO.getMinerId();
        List<FilBillSubAccountVO> filBillSubAccountVOList = new ArrayList<>();
        // Miner
        FilBillSubAccountVO minerFilBillSubAccountVO = new FilBillSubAccountVO();
        minerFilBillSubAccountVO.setName("Miner");
        minerFilBillSubAccountVO.setAddress(minerId);
        log.info("Miner账户：【{}】",JSON.toJSON(minerFilBillSubAccountVO));
        filBillSubAccountVOList.add(minerFilBillSubAccountVO);

        // Worker
        QueryWrapper<SysMinerInfo> workerQueryWrapper = new QueryWrapper<>();
        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        sysMinerInfo.setMinerId(minerId);
        workerQueryWrapper.setEntity(sysMinerInfo);
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.list(workerQueryWrapper);
        log.info("矿工列表：【{}】",JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList != null && sysMinerInfoList.size() > 0){
            FilBillSubAccountVO workerFilBillSubAccountVO = new FilBillSubAccountVO();
            workerFilBillSubAccountVO.setName("Worker");
            workerFilBillSubAccountVO.setAddress(sysMinerInfoList.get(0).getBalanceWorkerAddress());
            log.info("Worker账户：【{}】",JSON.toJSON(workerFilBillSubAccountVO));
            filBillSubAccountVOList.add(workerFilBillSubAccountVO);
        }

        // Controller
        QueryWrapper<FilMinerControlBalance> filMinerControlBalanceQueryWrapper = new QueryWrapper<>();
        FilMinerControlBalance qwFilMinerControlBalance = new FilMinerControlBalance();
        qwFilMinerControlBalance.setMinerId(minerId);
        filMinerControlBalanceQueryWrapper.setEntity(qwFilMinerControlBalance);
        List<FilMinerControlBalance> filMinerControlBalanceList = filMinerControlBalanceMapper.selectList(filMinerControlBalanceQueryWrapper);
        if (filMinerControlBalanceList != null && filMinerControlBalanceList.size() > 0){
            for (FilMinerControlBalance filMinerControlBalance:filMinerControlBalanceList){
                FilBillSubAccountVO controllerFilBillSubAccountVO = new FilBillSubAccountVO();
                controllerFilBillSubAccountVO.setName(filMinerControlBalance.getName());
                controllerFilBillSubAccountVO.setAddress(filMinerControlBalance.getAddress());
                filBillSubAccountVOList.add(controllerFilBillSubAccountVO);
            }
        }
        return filBillSubAccountVOList;
    }

    /*分页查询账单消息列表*/
    @Override
    public IPage<FilBillVO> selectFilBillPage(FilBillMethodBO filBillMethodBO) {
        String monthDate = filBillMethodBO.getMonthDate();
        String startDate = monthDate + "-01 00:00:00";
        String endDate = (DateUtils.getAssignEndDayOfMonth(Integer.valueOf(monthDate.substring(0,4)),Integer.valueOf(monthDate.substring(5,7))) + "").substring(0,19);
        Page<FilBillVO> page = new Page<>(filBillMethodBO.getPageNum(),filBillMethodBO.getPageSize());
        IPage<FilBillVO> filBillVOIPage = filBillMapper.selectFilBillPage(page,
                filBillMethodBO.getMinerId(),filBillMethodBO.getMethod(),filBillMethodBO.getType(),filBillMethodBO.getSubAccount(),startDate,endDate);
        return page;
    }

    /*查询账单汇总信息*/
    @Override
    public BillTotalVO selectFilBillTotal(FilBillMethodBO filBillMethodBO) {
        String monthDate = filBillMethodBO.getMonthDate();
        String startDate = monthDate + "-01 00:00:00";
        String endDate = (DateUtils.getAssignEndDayOfMonth(Integer.valueOf(monthDate.substring(0,4)),Integer.valueOf(monthDate.substring(5,7))) + "").substring(0,19);
        BillTotalVO billTotalVO = new BillTotalVO();

        // 收入
        BillMethodTotalVO in = new BillMethodTotalVO();
        List<BillMethodMoneyVO> billMethodMoneyVOInList = filBillMapper.selectBillMethodMoneyList("in",filBillMethodBO.getMinerId(),filBillMethodBO.getSubAccount(),startDate,endDate);
        log.info("查询收入方法、金额汇总信息list出参：",JSON.toJSON(billMethodMoneyVOInList));

        BillMethodMoneyVO blockAwardBillMethodMoneyVO = new BillMethodMoneyVO();
        blockAwardBillMethodMoneyVO.setMethod("FilBlockAward");
        // TODO 区块奖励以后做
        blockAwardBillMethodMoneyVO.setMoney(new BigDecimal("0"));
        log.info("区块奖励:【{}】",JSON.toJSON(blockAwardBillMethodMoneyVO));
        billMethodMoneyVOInList.add(blockAwardBillMethodMoneyVO);

        BigDecimal totalMoneyIn = new BigDecimal("0");
        if (billMethodMoneyVOInList != null && billMethodMoneyVOInList.size() > 0){
            for (BillMethodMoneyVO billMethodMoneyVOIn : billMethodMoneyVOInList){
                totalMoneyIn = totalMoneyIn.add(billMethodMoneyVOIn.getMoney());
            }
        }
        log.info("收入总金额：【{}】",totalMoneyIn);
        in.setTotal(totalMoneyIn);
        in.setBillMethodMoneyVOList(billMethodMoneyVOInList);
        billTotalVO.setIn(in);

        // 支出
        BillMethodTotalVO out = new BillMethodTotalVO();
        List<BillMethodMoneyVO> billMethodMoneyVOOutList = filBillMapper.selectBillMethodMoneyList("out",filBillMethodBO.getMinerId(),filBillMethodBO.getSubAccount(),startDate,endDate);
        log.info("查询支出方法、金额汇总信息list出参：",JSON.toJSON(billMethodMoneyVOOutList));
        BigDecimal totalMoneyOut = new BigDecimal("0");
        if (billMethodMoneyVOOutList != null && billMethodMoneyVOOutList.size() > 0){
            for (BillMethodMoneyVO billMethodMoneyVOOut : billMethodMoneyVOOutList){
                totalMoneyOut = totalMoneyOut.add(billMethodMoneyVOOut.getMoney());
            }
        }
        log.info("支出总金额：【{}】",totalMoneyOut);
        out.setTotal(totalMoneyOut);
        out.setBillMethodMoneyVOList(billMethodMoneyVOOutList);
        billTotalVO.setOut(out);

        return billTotalVO;
    }



}
