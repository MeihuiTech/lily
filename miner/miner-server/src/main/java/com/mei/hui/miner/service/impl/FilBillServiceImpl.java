package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBillMapper;
import com.mei.hui.miner.mapper.FilMinerControlBalanceMapper;
import com.mei.hui.miner.service.FilBillParamsService;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.FilBillTransactionsService;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    @Autowired
    private FilBillParamsService filBillParamsService;
    @Autowired
    private FilBillTransactionsService filBillTransactionsService;

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
        filBillVOIPage.getRecords().stream().forEach(v -> {
            if (v.getSender().equals(filBillMethodBO.getSubAccount())){
                v.setInOrOut(Constants.FILBILLOUT);
            } else if (v.getReceiver().equals(filBillMethodBO.getSubAccount())){
                v.setInOrOut(Constants.FILBILLIN);
            }
            if ("0".equals(v.getType())){
                v.setType(Constants.TYPENODEFEE);
            } else if ("1".equals(v.getType())){
                v.setType(Constants.TYPEBURNFEE);
            } else if ("2".equals(v.getType())){
                v.setType(Constants.TYPETRANSFER);
            }
        });
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

    /*上报FIL币账单*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportBillMq(FilBillReportBO filBillReportBO) {
        String minerId = filBillReportBO.getMiner();
        FilBill filBill = new FilBill();
        BeanUtils.copyProperties(filBillReportBO,filBill);
        filBill.setMinerId(minerId);
        filBill.setSender(filBillReportBO.getFrom());
        filBill.setReceiver(filBillReportBO.getTo());
        filBill.setMoney(filBillReportBO.getValue());
        filBill.setType(Constants.FILBILLTYPEBILL);
        filBill.setDateTime(LocalDateTime.ofEpochSecond(filBillReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8)));
        filBill.setCreateTime(LocalDateTime.now());
        log.info("保存FIL币账单入参：【{}】",filBill);
        filBillMapper.insert(filBill);

        // FIL币账单参数表
        String params = filBillReportBO.getParams();
        if (StringUtils.isNotEmpty(params)){
            FilBillParams filBillParams = new FilBillParams();
            filBillParams.setFilBillId(filBill.getId());
            filBillParams.setParams(params);
            filBillParams.setCreateTime(LocalDateTime.now());
            log.info("保存FIL币账单参数入参：【{}】",filBillParams);
            filBillParamsService.save(filBillParams);
        }

        // FIL币账单转账信息表
        List<FilBillTransactionsReportBO> filBillTransactionsReportBOList = filBillReportBO.getTransaction();
        if (filBillTransactionsReportBOList != null && filBillTransactionsReportBOList.size() > 0){
            for (FilBillTransactionsReportBO filBillTransactionsReportBO : filBillTransactionsReportBOList){
                log.info("filBillTransactionsReportBO:【{}】",JSON.toJSON(filBillTransactionsReportBO));
                FilBillTransactions filBillTransactions = new FilBillTransactions();
                BeanUtils.copyProperties(filBillTransactionsReportBO,filBillTransactions);
                filBillTransactions.setFilBillId(filBill.getId());
                String from = filBillTransactionsReportBO.getFrom();
                filBillTransactions.setSender(from);
                String to = filBillTransactionsReportBO.getTo();
                filBillTransactions.setReceiver(to);
                filBillTransactions.setMoney(filBillTransactionsReportBO.getValue());
                filBillTransactions.setCreateTime(LocalDateTime.now());
                String type = filBillTransactionsReportBO.getType();
                log.info("type：【{}】",type);
                if(Constants.TYPENODEFEE.equals(type)){
                    filBillTransactions.setType(0);
                } else if (Constants.TYPEBURNFEE.equals(type)){
                    filBillTransactions.setType(1);
                } else if (Constants.TYPETRANSFER.equals(type)){
                    filBillTransactions.setType(2);
                }

                FilBillMethodBO filBillMethodBO = new FilBillMethodBO();
                filBillMethodBO.setMinerId(minerId);
                List<FilBillSubAccountVO> filBillSubAccountVOList = selectFilBillSubAccountList(filBillMethodBO);
                log.info("矿工子账户下拉列表：【{}】",JSON.toJSON(filBillSubAccountVOList));
                log.info("from：【{}】,to：【{}】",from,to);
                if (filBillSubAccountVOList.contains(from) && filBillSubAccountVOList.contains(to)){
                    filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEINSIDE);
                } else {
                    filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEOUTSIDE);
                    if (filBillSubAccountVOList.contains(from)){
                        filBillTransactions.setOutsideType(Constants.OUTSIDETYPEOUT);
                    } else {
                        filBillTransactions.setOutsideType(Constants.OUTSIDETYPEIN);
                    }
                }

                log.info("保存FIL币账单转账信息表入参：【{}】",filBillTransactions);
                filBillTransactionsService.save(filBillTransactions);
            }
        }
    }

    /*在FIL币账单消息详情表里手动插入一条区块奖励数据*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFilBillBlockAward(FilBlockAwardReportBO filBlockAwardReportBO) {
        String minerId = filBlockAwardReportBO.getMiner();
        FilBill filBill = new FilBill();
        filBill.setMinerId(minerId);
        filBill.setHeight(filBlockAwardReportBO.getHeight());
        filBill.setParentBaseFee(filBlockAwardReportBO.getParentBaseFee());
        filBill.setType(Constants.FILBILLTYPEBLOCKAWARD);
        filBill.setDateTime(LocalDateTime.ofEpochSecond(filBlockAwardReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8)));
        filBill.setCreateTime(LocalDateTime.now());
        log.info("保存FIL币账单入参：【{}】",filBill);
        filBillMapper.insert(filBill);

        // FIL币账单转账信息表
        FilBillTransactions filBillTransactions = new FilBillTransactions();
        filBillTransactions.setFilBillId(filBill.getId());
        filBillTransactions.setSender(Constants.BLOCKAWARDSEND);
        filBillTransactions.setReceiver(minerId);
        filBillTransactions.setMoney(filBlockAwardReportBO.getMinerFee().add(filBlockAwardReportBO.getBlockReward()));
        filBillTransactions.setType(Constants.TYPEBLOCKAWARDTHREE);
        filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEOUTSIDE);
        filBillTransactions.setOutsideType(Constants.OUTSIDETYPEIN);
        filBillTransactions.setCreateTime(LocalDateTime.now());
        log.info("保存FIL币账单转账信息表入参：【{}】",filBillTransactions);
        filBillTransactionsService.save(filBillTransactions);
    }




}
