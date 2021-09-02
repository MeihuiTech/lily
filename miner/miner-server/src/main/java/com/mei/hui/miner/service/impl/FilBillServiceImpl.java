package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillDayAgg;
import com.mei.hui.miner.entity.FilBillParams;
import com.mei.hui.miner.entity.FilBillTransactions;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBillDayAggMapper;
import com.mei.hui.miner.mapper.FilBillMapper;
import com.mei.hui.miner.mapper.FilMinerControlBalanceMapper;
import com.mei.hui.miner.service.FilBillParamsService;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.FilBillTransactionsService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private FilBillDayAggMapper filBillDayAggMapper;
    @Autowired
    private RedisUtil redisUtil;


    /*上报FIL币账单*/
    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void reportBillMq(FilBillReportBO filBillReportBO) {
        String minerId = filBillReportBO.getMiner();
        String method = filBillReportBO.getMethod();
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

            FilBillMethodBO filBillMethodBO = new FilBillMethodBO();
            filBillMethodBO.setMinerId(minerId);
            List<FilBillSubAccountVO> filBillSubAccountVOList = selectFilBillSubAccountList(filBillMethodBO);
            log.info("矿工子账户下拉列表：【{}】",JSON.toJSON(filBillSubAccountVOList));
            List<String> addressList = new ArrayList<>();
            filBillSubAccountVOList.stream().forEach(v->{
                addressList.add(v.getAddress());
            });
            log.info("矿工子账户地址列表addressList：【{}】",addressList);

            List<FilBillTransactions> filBillTransactionsList = new ArrayList<>();
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
                    filBillTransactions.setType(Constants.TYPENODEFEEZERO);
                } else if (Constants.TYPEBURNFEE.equals(type)){
                    filBillTransactions.setType(Constants.TYPEBURNFEEONE);
                } else if (Constants.TYPETRANSFER.equals(type)){
                    // 转账
                    if (Constants.FILBILLMETHODSEND.equals(method) || Constants.FILBILLMETHODPROPOSE.equals(method) || Constants.FILBILLMETHODREPORTCONSENSUSFAULT.equals(method)){
                        filBillTransactions.setType(Constants.TYPETRANSFERTWO);
                    } else {
                        // 其它
                        filBillTransactions.setType(Constants.TYPEOTHERFOUR);
                    }
                }

                log.info("from：【{}】,to：【{}】",from,to);
                if (addressList.contains(from) && addressList.contains(to) && !Constants.TYPENODEFEE.equals(type) && !Constants.TYPEBURNFEE.equals(type)){
                    filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEINSIDE);
                } else {
                    filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEOUTSIDE);
                    if (addressList.contains(from) || Constants.TYPENODEFEE.equals(type) || Constants.TYPEBURNFEE.equals(type)){
                        filBillTransactions.setOutsideType(Constants.OUTSIDETYPEOUT);
                    } else {
                        filBillTransactions.setOutsideType(Constants.OUTSIDETYPEIN);
                    }
                }

                log.info("保存FIL币账单转账信息表入参：【{}】",filBillTransactions);
                filBillTransactionsList.add(filBillTransactions);


                /**
                 * 1.根据miner_id、date先查redis里是否有数据，如果没有新建一条，redis过期时间设置为24小时，
                 * 判断是收入还是支出，比如是收入，redis里的收入+新进来的数据的值，
                 * 然后（update where 根据miner_id、date ）数据库里的这天的数据
                 *
                 * update where 根据miner_id、date ，判断返回值：如果是0，插入
                 * insertorupdate
                 */



            }
            if (filBillTransactionsList.size() > 0){
                log.info("批量保存FIL币账单转账信息表入参：【{}】",filBillTransactionsList);
                filBillTransactionsService.saveBatch(filBillTransactionsList);
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
        filBillTransactions.setMoney((filBlockAwardReportBO.getMinerFee() == null ? BigDecimal.ZERO:filBlockAwardReportBO.getMinerFee()).add(filBlockAwardReportBO.getBlockReward()));
        filBillTransactions.setType(Constants.TYPEBLOCKAWARDTHREE);
        filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEOUTSIDE);
        filBillTransactions.setOutsideType(Constants.OUTSIDETYPEIN);
        filBillTransactions.setCreateTime(LocalDateTime.now());
        log.info("保存FIL币账单转账信息表入参：【{}】",filBillTransactions);
        filBillTransactionsService.save(filBillTransactions);
    }

    /*分页查询日账单列表*/
    @Override
    public IPage<FilBillDayAggVO> selectFilBillDayAggPage(FilBillMonthBO filBillMonthBO) {
        String monthDate = filBillMonthBO.getMonthDate();
        String startDate = monthDate + "-01";
        String endDate = (DateUtils.getAssignEndDayOfMonth(Integer.valueOf(monthDate.substring(0,4)),Integer.valueOf(monthDate.substring(5,7))) + "").substring(0,10);
        Page<FilBillDayAggVO> page = new Page<>(filBillMonthBO.getPageNum(),filBillMonthBO.getPageSize());
        IPage<FilBillDayAggVO> filBillDayAggVOIPage = filBillMapper.selectFilBillDayAggPage(page,filBillMonthBO.getMinerId(),startDate,endDate);
        log.info("分页查询日账单列表出参：【{}】",JSON.toJSON(filBillDayAggVOIPage));
        filBillDayAggVOIPage.getRecords().stream().forEach(v->{
            v.setDate((v.getDate()+"").substring(0,10));
        });
        return filBillDayAggVOIPage;
    }

    /*账单月汇总*/
    @Override
    public BillTotalVO selectFilBillmonthAgg(FilBillMonthBO filBillMonthBO) {
        String monthDate = filBillMonthBO.getMonthDate();
        String startDate = monthDate + "-01 00:00:00";
        // 昨天的结束日期
        String endDate = "";
        // 如果是当月，查询到昨天的24点，如果不是当月，查询到当月月底
        if(DateUtils.getDate().substring(0,7).equals(monthDate)){
            endDate = (DateUtils.getYesterDayDateYmd()+" 23:59:59");
        } else {
            endDate = (DateUtils.getAssignEndDayOfMonth(Integer.valueOf(monthDate.substring(0,4)),Integer.valueOf(monthDate.substring(5,7))) + "").substring(0,19);
        }
        String minerId = filBillMonthBO.getMinerId();
        BillTotalVO billTotalVO = new BillTotalVO();

        // 收入
        BillMethodTotalVO in = new BillMethodTotalVO();
        List<BillMethodMoneyVO> inBillMethodMoneyVOList = new ArrayList<>();

        // 收入-转账
        BillMethodMoneyVO inTransferBillMethodMoneyVO = new BillMethodMoneyVO();
        inTransferBillMethodMoneyVO.setMethod("转账");
        BigDecimal inTransferMoney = filBillMapper.selectFilBillTransferDateAgg(1,minerId,startDate,endDate);
        inTransferMoney = inTransferMoney == null?BigDecimal.ZERO:inTransferMoney;
        log.info("查询账单月汇总转账收入出参：【{}】",inTransferMoney);
        inTransferBillMethodMoneyVO.setMoney(inTransferMoney);
        inBillMethodMoneyVOList.add(inTransferBillMethodMoneyVO);

        // 收入-区块奖励
        BillMethodMoneyVO inBlockAwardBillMethodMoneyVO = new BillMethodMoneyVO();
        inBlockAwardBillMethodMoneyVO.setMethod("区块奖励");
        BigDecimal inBlockAwardMoney = filBillMapper.selectFilBillinBlockAwardDateAgg(minerId,startDate,endDate);
        inBlockAwardMoney = inBlockAwardMoney == null?BigDecimal.ZERO:inBlockAwardMoney;
        log.info("查询账单月汇总区块奖励收入出参：【{}】",inBlockAwardMoney);
        inBlockAwardBillMethodMoneyVO.setMoney(inBlockAwardMoney);
        inBillMethodMoneyVOList.add(inBlockAwardBillMethodMoneyVO);

        in.setBillMethodMoneyVOList(inBillMethodMoneyVOList);
        in.setTotal(inTransferMoney.add(inBlockAwardMoney));
        billTotalVO.setIn(in);

        // 支出
        BillMethodTotalVO out = new BillMethodTotalVO();
        List<BillMethodMoneyVO> outBillMethodMoneyVOList = new ArrayList<>();

        // 支出-转账
        BillMethodMoneyVO outTransferBillMethodMoneyVO = new BillMethodMoneyVO();
        outTransferBillMethodMoneyVO.setMethod("转账");
        BigDecimal outTransferMoney = filBillMapper.selectFilBillTransferDateAgg(0,minerId,startDate,endDate);
        outTransferMoney = outTransferMoney == null?BigDecimal.ZERO:outTransferMoney;
        log.info("查询账单月汇总转账支出出参：【{}】",outTransferMoney);
        outTransferBillMethodMoneyVO.setMoney(outTransferMoney);
        outBillMethodMoneyVOList.add(outTransferBillMethodMoneyVO);

        // 支出-矿工手续费
        BillMethodMoneyVO outNodeFeeBillMethodMoneyVO = new BillMethodMoneyVO();
        outNodeFeeBillMethodMoneyVO.setMethod("存储手续费");
        BigDecimal outNodeFeeMoney = filBillMapper.selectFilBillOutFeeDateAgg(0,minerId,startDate,endDate);
        outNodeFeeMoney = outNodeFeeMoney == null?BigDecimal.ZERO:outNodeFeeMoney;
        log.info("查询账单按照日期范围汇总矿工手续费支出出参：【{}】",outNodeFeeMoney);
        outNodeFeeBillMethodMoneyVO.setMoney(outNodeFeeMoney);
        outBillMethodMoneyVOList.add(outNodeFeeBillMethodMoneyVO);

        // 支出-燃烧手续费
        BillMethodMoneyVO outBurnFeeBillMethodMoneyVO = new BillMethodMoneyVO();
        outBurnFeeBillMethodMoneyVO.setMethod("燃烧手续费");
        BigDecimal outBurnFeeMoney = filBillMapper.selectFilBillOutFeeDateAgg(1,minerId,startDate,endDate);
        outBurnFeeMoney = outBurnFeeMoney == null?BigDecimal.ZERO:outBurnFeeMoney;
        log.info("查询账单按照日期范围汇总燃烧手续费支出出参：【{}】",outBurnFeeMoney);
        outBurnFeeBillMethodMoneyVO.setMoney(outBurnFeeMoney);
        outBillMethodMoneyVOList.add(outBurnFeeBillMethodMoneyVO);

        // 支出-其它
        BillMethodMoneyVO outOtherBillMethodMoneyVO = new BillMethodMoneyVO();
        outOtherBillMethodMoneyVO.setMethod("其它");
        BigDecimal outAllMoney = filBillMapper.selectFilBillOutAllDateAgg(minerId,startDate,endDate);
        log.info("查询账单按照日期范围汇总所有外部交易支出出参：【{}】",outAllMoney);
        outAllMoney = outAllMoney == null?BigDecimal.ZERO:outAllMoney;
        log.info("查询账单按照日期范围汇总所有外部交易支出出参2：【{}】",outAllMoney);
//        log.info("outTransferMoney：【{}】",outTransferMoney);
//        BigDecimal outOtherMoney = outAllMoney.subtract(outTransferMoney);
//        log.info("支出-其它：【{}】",outOtherMoney);
        outOtherBillMethodMoneyVO.setMoney(outAllMoney);
        outBillMethodMoneyVOList.add(outOtherBillMethodMoneyVO);

        out.setBillMethodMoneyVOList(outBillMethodMoneyVOList);
        out.setTotal(outTransferMoney.add(outNodeFeeMoney).add(outBurnFeeMoney).add(outAllMoney));
        billTotalVO.setOut(out);

        return billTotalVO;
    }

    /*分页查询日账单详情列表*/
    @Override
    public IPage<FilBillVO> selectFilBillTransactionsPage(FilBillMonthBO filBillMonthBO) {
        FilBillDayAgg filBillDayAgg = filBillDayAggMapper.selectById(filBillMonthBO.getId());
        if (filBillDayAgg == null){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"id不存在");
        }
        String minerId = filBillDayAgg.getMinerId();
        LocalDate date = filBillDayAgg.getDate();
        String dateStr = date.toString();// .substring(0,10)
        String startDate = dateStr + " 00:00:00";
        String endDate = dateStr + " 23:59:59";
        Page<FilBillVO> page = new Page<>(filBillMonthBO.getPageNum(),filBillMonthBO.getPageSize());
        log.info("分页查询日账单详情列表入参page：【{}】，minerId：【{}】，startDate：【{}】，endDate：【{}】",JSON.toJSON(page),minerId,startDate,endDate);
        IPage<FilBillVO> filBillVOIPage = filBillMapper.selectFilBillTransactionsPage(page,minerId,startDate,endDate,filBillMonthBO.getType(),filBillMonthBO.getOutsideType());
        log.info("分页查询日账单详情列表出参：【{}】",JSON.toJSON(filBillVOIPage));
        return filBillVOIPage;
    }

    /*新增FIL币账单消息每天汇总表*/
    @Override
    public Integer insertFilBillDayAgg(String minerId, String startDate, String endDate,LocalDate date) {
        FilBillDayAgg filBillDayAgg = new FilBillDayAgg();
        filBillDayAgg.setMinerId(minerId);
        filBillDayAgg.setDate(date);
        filBillDayAgg.setCreateTime(LocalDateTime.now());
        List<FilBillTransactions> filBillTransactionsList = filBillMapper.selectFilBillTransactionsMoney(minerId, startDate, endDate);
        log.info("FIL币账单消息每天汇总收支：【{}】",JSON.toJSON(filBillTransactionsList));
        if (filBillTransactionsList != null && filBillTransactionsList.size() > 0){
            for (FilBillTransactions filBillTransactions:filBillTransactionsList){
                Integer outsideType = filBillTransactions.getOutsideType();
                BigDecimal money = filBillTransactions.getMoney();
                money = money == null?BigDecimal.ZERO:money;
                if (Constants.FILBILLIN.equals(outsideType)){
                    filBillDayAgg.setInMoney(money);
                } else if (Constants.FILBILLOUT.equals(outsideType)) {
                    filBillDayAgg.setOutMoney(money);
                }
            }
            filBillDayAgg.setBalance((filBillDayAgg.getInMoney() == null?BigDecimal.ZERO:filBillDayAgg.getInMoney()).subtract(filBillDayAgg.getOutMoney() == null?BigDecimal.ZERO:filBillDayAgg.getOutMoney()));
        } else {
            filBillDayAgg.setInMoney(BigDecimal.ZERO);
            filBillDayAgg.setOutMoney(BigDecimal.ZERO);
            filBillDayAgg.setBalance(BigDecimal.ZERO);
        }
        Integer count = filBillDayAggMapper.insert(filBillDayAgg);
        return count;
    }

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

        // Worker，从redis中获取
        String balanceWorkerAddress = redisUtil.hget(Constants.REDISMINERADDRESS + minerId,"Worker");
        FilBillSubAccountVO workerFilBillSubAccountVO = new FilBillSubAccountVO();
        workerFilBillSubAccountVO.setName("Worker");
        workerFilBillSubAccountVO.setAddress(balanceWorkerAddress);
        log.info("Worker账户：【{}】",JSON.toJSON(workerFilBillSubAccountVO));
        filBillSubAccountVOList.add(workerFilBillSubAccountVO);

        // Controller，从redis中获取
        Map<String,String> controllerMap = redisUtil.hgetall(Constants.REDISMINERADDRESS + minerId);
        for (Map.Entry<String,String> entry:controllerMap.entrySet()){
            if (!entry.getKey().equals("Miner") && !entry.getKey().equals("Worker")){
                FilBillSubAccountVO controllerFilBillSubAccountVO = new FilBillSubAccountVO();
                controllerFilBillSubAccountVO.setName(entry.getKey());
                controllerFilBillSubAccountVO.setAddress(entry.getValue());
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
                v.setInOrOut(Constants.FILBILLOUT.toString());
            } else if (v.getReceiver().equals(filBillMethodBO.getSubAccount())){
                v.setInOrOut(Constants.FILBILLIN.toString());
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




}
