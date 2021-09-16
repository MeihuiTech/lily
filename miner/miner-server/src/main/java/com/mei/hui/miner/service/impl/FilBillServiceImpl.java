package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.*;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.mapper.FilBillDayAggMapper;
import com.mei.hui.miner.mapper.FilBillMapper;
import com.mei.hui.miner.mapper.FilMinerControlBalanceMapper;
import com.mei.hui.miner.service.*;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.IdUtils;
import com.mei.hui.util.MyException;
import com.mei.hui.util.SystemConstants;
import com.mei.hui.util.html.DateFormatEnum;
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
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private FilBillDayAggService filBillDayAggService;
    @Autowired
    private FilBillBalanceDayAggService filBillBalanceDayAggService;


    /*上报FIL币账单*/
    @Override
//    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void reportBillMq(FilBillReportBO filBillReportBO,List<FilBill> filBillList,List<FilBillTransactions> filBillTransactionsList,FilBillDayAggArgsVO filBillDayAggArgsVO) {
        log.info("上报FIL币账单：filBillReportBO：【{}】,filBillList：【{}】,filBillTransactionsList：【{}】,filBillDayAggArgsVO：【{}】",
                filBillReportBO,filBillList,filBillTransactionsList,filBillDayAggArgsVO);
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
        filBill.setId(IdUtils.simpleUUID());
        log.info("往filBillList添加单个FIL币账单消息详情表：【{}】",filBill);
        filBillList.add(filBill);

        // FIL币账单转账信息表
        List<FilBillTransactionsReportBO> filBillTransactionsReportBOList = filBillReportBO.getTransaction();
//        log.info("FIL币账单转账信息表列表filBillTransactionsReportBOList：【{}】",JSON.toJSON(filBillTransactionsReportBOList));
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

            for (FilBillTransactionsReportBO filBillTransactionsReportBO : filBillTransactionsReportBOList){
//                log.info("filBillTransactionsReportBO:【{}】",JSON.toJSON(filBillTransactionsReportBO));

                FilBillTransactions filBillTransactions = packageFilBillTransactions(filBill.getId(),method,addressList,filBillTransactionsReportBO);

                log.info("往filBillTransactionsList添加单个FIL币账单转账信息表：【{}】",filBillTransactions);
                filBillTransactionsList.add(filBillTransactions);
            }

            /**
             * 1.根据miner_id、date先查redis里是否有数据，如果没有，数据库里新建一条，放到redis里，redis过期时间设置为25小时，
             * 然后（update where 根据miner_id、date ）数据库里的这天的数据
             */
            String date = DateUtils.lDTLocalDateTimeFormatYMD(filBill.getDateTime());
            insertOrUpdateFilBillDayAggByMinerIdAndDate(minerId,date,method,filBill.getDateTime().toLocalDate(),filBillTransactionsList,filBillDayAggArgsVO);
        }

    }

    /**
     * 组装 FIL币账单转账信息表
     * @param filBillId
     * @param method
     * @param addressList
     * @param filBillTransactionsReportBO
     * @return
     */
    public FilBillTransactions packageFilBillTransactions(String filBillId,String method,List<String> addressList,FilBillTransactionsReportBO filBillTransactionsReportBO){
        log.info("组装 FIL币账单转账信息表入参filBillId：【{}】,method：【{}】,addressList：【{}】,filBillTransactionsReportBO：【{}】",filBillId,method,addressList,JSON.toJSON(filBillTransactionsReportBO));
        FilBillTransactions filBillTransactions = new FilBillTransactions();
        BeanUtils.copyProperties(filBillTransactionsReportBO,filBillTransactions);
        filBillTransactions.setFilBillId(filBillId);
        String from = filBillTransactionsReportBO.getFrom();
        filBillTransactions.setSender(from);
        String to = filBillTransactionsReportBO.getTo();
        filBillTransactions.setReceiver(to);
        filBillTransactions.setMoney(filBillTransactionsReportBO.getValue());
        filBillTransactions.setCreateTime(LocalDateTime.now());
        String type = filBillTransactionsReportBO.getType();
        log.info("type：【{}】",type);
        if(Constants.TYPENODEFEE.equals(type)){
            log.info(Constants.TYPENODEFEE);
            filBillTransactions.setType(Constants.TYPENODEFEEZERO);
        } else if (Constants.TYPEBURNFEE.equals(type)){
            log.info(Constants.TYPEBURNFEE);
            filBillTransactions.setType(Constants.TYPEBURNFEEONE);
        } else if (Constants.TYPETRANSFER.equals(type)){
            // 转账
            if (Constants.FILBILLMETHODSEND.equals(method) || Constants.FILBILLMETHODPROPOSE.equals(method) || Constants.FILBILLMETHODREPORTCONSENSUSFAULT.equals(method)){
                log.info(Constants.TYPETRANSFER);
                filBillTransactions.setType(Constants.TYPETRANSFERTWO);
            } else {
                // 其它
                log.info(Constants.TYPEOTHER);
                filBillTransactions.setType(Constants.TYPEOTHERFOUR);
            }
        }

        log.info("from：【{}】,to：【{}】",from,to);
        if (addressList.contains(from) && addressList.contains(to) && !Constants.TYPENODEFEE.equals(type) && !Constants.TYPEBURNFEE.equals(type)){
            log.info(Constants.TRANSACTIONTYPEINSIDE+"");
            filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEINSIDE);
        } else {
            log.info(Constants.TRANSACTIONTYPEOUTSIDE+"");
            filBillTransactions.setTransactionType(Constants.TRANSACTIONTYPEOUTSIDE);
            if (addressList.contains(from) || Constants.TYPENODEFEE.equals(type) || Constants.TYPEBURNFEE.equals(type)){
                log.info(Constants.OUTSIDETYPEOUT+"");
                filBillTransactions.setOutsideType(Constants.OUTSIDETYPEOUT);
            } else {
                log.info(Constants.OUTSIDETYPEIN+"");
                filBillTransactions.setOutsideType(Constants.OUTSIDETYPEIN);
            }
        }
        return filBillTransactions;
    }

    /**
     * 更新或者插入 FIL币账单消息每天汇总表
     * update where 根据miner_id、date ，判断返回值：如果是0，插入
     * @param minerId
     * @param date
     * @param method
     * @param dateTime
     * @param filBillTransactionsList
     */
    public void insertOrUpdateFilBillDayAggByMinerIdAndDate(String minerId,String date,String method,LocalDate dateTime,List<FilBillTransactions> filBillTransactionsList,FilBillDayAggArgsVO filBillDayAggArgsVO){
        log.info("更新或者插入FIL币账单消息每天汇总表入参minerId：【{}】，date：【{}】，method：【{}】，dateTime：【{}】，" +
                "filBillTransactionsList：【{}】，filBillDayAggArgsVO：【{}】",minerId, date, method, dateTime, JSON.toJSON(filBillTransactionsList),JSON.toJSON(filBillDayAggArgsVO));

        for (FilBillTransactions filBillTransactions: filBillTransactionsList){
            if (Constants.TRANSACTIONTYPEINSIDE.equals(filBillTransactions.getTransactionType())){
                log.info("内部交易，跳过该条记录，执行下一条：【{}】",JSON.toJSON(filBillTransactions));
                continue;
            }

            log.info("外部交易，接着走下面流程：【{}】",JSON.toJSON(filBillTransactions));
            filBillDayAggArgsVO.setInMoney(filBillDayAggArgsVO.getInMoney()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getInMoney());
            filBillDayAggArgsVO.setOutMoney(filBillDayAggArgsVO.getOutMoney()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getOutMoney());
            filBillDayAggArgsVO.setBalance(filBillDayAggArgsVO.getBalance()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getBalance());
            filBillDayAggArgsVO.setInTransfer(filBillDayAggArgsVO.getInTransfer()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getInTransfer());
            filBillDayAggArgsVO.setInBlockAward(filBillDayAggArgsVO.getInBlockAward()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getInBlockAward());
            filBillDayAggArgsVO.setOutTransfer(filBillDayAggArgsVO.getOutTransfer()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getOutTransfer());
            filBillDayAggArgsVO.setOutNodeFee(filBillDayAggArgsVO.getOutNodeFee()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getOutNodeFee());
            filBillDayAggArgsVO.setOutBurnFee(filBillDayAggArgsVO.getOutBurnFee()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getOutBurnFee());
            filBillDayAggArgsVO.setOutOther(filBillDayAggArgsVO.getOutOther()== null?BigDecimal.ZERO:filBillDayAggArgsVO.getOutOther());
//            log.info("filBillDayAggArgsVO:【{}】",JSON.toJSON(filBillDayAggArgsVO));

            Integer type = filBillTransactions.getType();
            BigDecimal money = filBillTransactions.getMoney();
            Integer outsideType = filBillTransactions.getOutsideType();
//            log.info("type：【{}】，money：【{}】，outsideType：【{}】",type,money,outsideType);
            if (Constants.TYPENODEFEEZERO.equals(type)){
                filBillDayAggArgsVO.setOutNodeFee(money.add(filBillDayAggArgsVO.getOutNodeFee()));
                log.info("支出-矿工手续费：【{}】",filBillDayAggArgsVO.getOutNodeFee());
            } else if (Constants.TYPEBURNFEEONE.equals(type)){
                filBillDayAggArgsVO.setOutBurnFee(money.add(filBillDayAggArgsVO.getOutBurnFee()));
                log.info("支出-燃烧手续费：【{}】",filBillDayAggArgsVO.getOutBurnFee());
            } else if (Constants.TYPETRANSFERTWO.equals(type)){
                if (Constants.FILBILLMETHODSEND.equals(method) || Constants.FILBILLMETHODPROPOSE.equals(method) || Constants.FILBILLMETHODREPORTCONSENSUSFAULT.equals(method)){
                    if (Constants.OUTSIDETYPEOUT.equals(outsideType)){
                        filBillDayAggArgsVO.setOutTransfer(money.add(filBillDayAggArgsVO.getOutTransfer()));
                        log.info("支出-转账：【{}】",filBillDayAggArgsVO.getOutTransfer());
                    } else {
                        filBillDayAggArgsVO.setInTransfer(money.add(filBillDayAggArgsVO.getInTransfer()));
                        log.info("收入-转账：【{}】",filBillDayAggArgsVO.getInTransfer());
                    }
                }
            } else if (Constants.TYPEBLOCKAWARDTHREE.equals(type)){
                filBillDayAggArgsVO.setInBlockAward(money.add(filBillDayAggArgsVO.getInBlockAward()));
                log.info("收入-区块奖励：【{}】",filBillDayAggArgsVO.getInBlockAward());
            } else if (Constants.TYPEOTHERFOUR.equals(type)){
                Integer transactionType = filBillTransactions.getTransactionType();
                if (Constants.TRANSACTIONTYPEOUTSIDE.equals(transactionType) && Constants.OUTSIDETYPEOUT.equals(outsideType)){
                    filBillDayAggArgsVO.setOutOther(money.add(filBillDayAggArgsVO.getOutOther()));
                    log.info("支出-其它：【{}】",filBillDayAggArgsVO.getOutOther());
                }
            }
        }

        filBillDayAggArgsVO.setInMoney(filBillDayAggArgsVO.getInTransfer().add(filBillDayAggArgsVO.getInBlockAward()));
        filBillDayAggArgsVO.setOutMoney(filBillDayAggArgsVO.getOutTransfer().add(filBillDayAggArgsVO.getOutNodeFee()).add(filBillDayAggArgsVO.getOutBurnFee()).add(filBillDayAggArgsVO.getOutOther()));
        filBillDayAggArgsVO.setBalance(filBillDayAggArgsVO.getInMoney().subtract(filBillDayAggArgsVO.getOutMoney()));
        log.info("更新或者插入 FIL币账单消息每天汇总表  完成最后的filBillDayAggArgsVO：【{}】",JSON.toJSON(filBillDayAggArgsVO));
    }

    /*更新或者插入所有的FIL币账单消息每天汇总表*/
    @Override
    public void insertOrUpdateFilBillDayAggByMinerIdAndDateAll(String minerId, LocalDateTime dateTime, FilBillDayAggArgsVO filBillDayAggArgsVO) {
        String date = DateUtils.lDTLocalDateTimeFormatYMD(dateTime);
        String redisKey = String.format(Constants.FILBILLDAYAGGKEY, minerId, date);
        String redisValue = redisUtil.get(redisKey);
        log.info("从redis里查询FIL币账单消息每天汇总表出参：【{}】",redisValue);
        if (StringUtils.isEmpty(redisValue)){
            log.info("往redis和数据库里都里插入数据：【{}】",JSON.toJSON(filBillDayAggArgsVO));
            redisUtil.set(redisKey,"0",25,TimeUnit.HOURS);
            insertFilBillDayAgg(minerId, dateTime.toLocalDate(),  filBillDayAggArgsVO);
            return;
        }

        // 根据矿工id、日期更新所有的收入和支出
        // 上报账单和上报区块奖励同时操作日统计表，所以需要加锁
        String redisLock = String.format(Constants.FILBILLDAYAGGLOCK,minerId);
        log.info("根据矿工id、日期更新所有的收入和支出redisLock：【{}】",redisLock);
        redisUtil.lock(redisLock);
        filBillDayAggMapper.updateFilBillDayAggByMinerIdAndDate(minerId,date,filBillDayAggArgsVO.getInMoney(),filBillDayAggArgsVO.getOutMoney(),filBillDayAggArgsVO.getBalance(),
                filBillDayAggArgsVO.getInTransfer(),filBillDayAggArgsVO.getInBlockAward(),filBillDayAggArgsVO.getOutTransfer(),filBillDayAggArgsVO.getOutNodeFee(),
                filBillDayAggArgsVO.getOutBurnFee(),filBillDayAggArgsVO.getOutOther(),Constants.FILBILLDAYAGGTYPENORMAL);
        redisUtil.unlock(redisLock);
    }

    /**
     * 单条插入FIL币账单消息每天汇总表
     * @param minerId
     * @param date
     * @param inMoney
     * @param outMoney
     * @param balance
     * @param inTransfer
     * @param inBlockAward
     * @param outTransfer
     * @param outNodeFee
     * @param outBurnFee
     * @param outOther
     * @return
     */
    public Integer insertFilBillDayAgg(String minerId, LocalDate dateTime, FilBillDayAggArgsVO filBillDayAggArgsVO){
        log.info("单条插入FIL币账单消息每天汇总表入参：filBillDayAggArgsVO：【{}】",filBillDayAggArgsVO);
        FilBillDayAgg filBillDayAgg = new FilBillDayAgg();
        filBillDayAgg.setMinerId(minerId);
        filBillDayAgg.setDate(dateTime);
        filBillDayAgg.setCreateTime(LocalDateTime.now());
        filBillDayAgg.setInMoney(filBillDayAggArgsVO.getInMoney());
        filBillDayAgg.setOutMoney(filBillDayAggArgsVO.getOutMoney());
        filBillDayAgg.setBalance(filBillDayAggArgsVO.getBalance());
        filBillDayAgg.setInTransfer(filBillDayAggArgsVO.getInTransfer());
        filBillDayAgg.setInBlockAward(filBillDayAggArgsVO.getInBlockAward());
        filBillDayAgg.setOutTransfer(filBillDayAggArgsVO.getOutTransfer());
        filBillDayAgg.setOutNodeFee(filBillDayAggArgsVO.getOutNodeFee());
        filBillDayAgg.setOutBurnFee(filBillDayAggArgsVO.getOutBurnFee());
        filBillDayAgg.setOutOther(filBillDayAggArgsVO.getOutOther());
        filBillDayAgg.setType(Constants.FILBILLDAYAGGTYPENORMAL);
        log.info("单条插入FIL币账单消息每天汇总表：【{}】",JSON.toJSON(filBillDayAgg));
        return filBillDayAggMapper.insert(filBillDayAgg);
    }

    /*批量保存FIL币账单消息详情表、FIL币账单转账信息表，实时计算FIL币账单消息每天汇总表数据*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBatchReportBillMq(String minerId, LocalDateTime dateTime, List<FilBill> filBillList, List<FilBillTransactions> allFilBillTransactionsList,FilBillDayAggArgsVO filBillDayAggArgsVO) {
        log.info("批量保存FIL币账单消息详情表入参：【{}】",JSON.toJSON(filBillList));
        saveBatch(filBillList);
        log.info("批量保存FIL币账单转账信息表入参：【{}】",JSON.toJSON(allFilBillTransactionsList));
        filBillTransactionsService.saveBatch(allFilBillTransactionsList);
        log.info("更新或者插入所有的FIL币账单消息每天汇总表minerId：【{}】，dateTime：【{}】，filBillDayAggArgsVO：【{}】",minerId,dateTime,JSON.toJSON(filBillDayAggArgsVO));
        insertOrUpdateFilBillDayAggByMinerIdAndDateAll(minerId,dateTime,filBillDayAggArgsVO);
    }

    /*在FIL币账单消息详情表里手动插入一条区块奖励数据*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertFilBillBlockAward(FilBlockAwardReportBO filBlockAwardReportBO,FilBillDayAggArgsVO filBillDayAggArgsVO) {
        String minerId = filBlockAwardReportBO.getMiner();
        FilBill filBill = insertFilBill(minerId,Constants.FILBILLTYPEBLOCKAWARD,LocalDateTime.ofEpochSecond(filBlockAwardReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8)));

        FilBillTransactions filBillTransactions = insertFilBillTransactions(filBill.getId(),Constants.BLOCKAWARDSEND,minerId,
                (filBlockAwardReportBO.getMinerFee() == null ? BigDecimal.ZERO:filBlockAwardReportBO.getMinerFee()).add(filBlockAwardReportBO.getBlockReward()),
                Constants.TYPEBLOCKAWARDTHREE,Constants.TRANSACTIONTYPEOUTSIDE,Constants.OUTSIDETYPEIN);

        String date = DateUtils.lDTLocalDateTimeFormatYMD(filBill.getDateTime());
        List<FilBillTransactions> filBillTransactionsList = new ArrayList<>();
        filBillTransactionsList.add(filBillTransactions);
        insertOrUpdateFilBillDayAggByMinerIdAndDate(minerId,date,null,filBill.getDateTime().toLocalDate(),filBillTransactionsList, filBillDayAggArgsVO);
    }

    /**
     * 单个保存FIL币账单
     * @param minerId
     * @param type
     * @param dateTime
     */
    public FilBill insertFilBill(String minerId,Integer type,LocalDateTime dateTime){
        FilBill filBill = new FilBill();
        filBill.setId(IdUtils.simpleUUID());
        filBill.setMinerId(minerId);
        filBill.setType(type);
        filBill.setDateTime(dateTime);
        filBill.setCreateTime(LocalDateTime.now());
        log.info("保存FIL币账单入参：【{}】",JSON.toJSON(filBill));
        filBillMapper.insert(filBill);
        return filBill;
    }

    /**
     * 单个保存FIL币账单转账信息表
     * @return
     */
    public FilBillTransactions insertFilBillTransactions(String filBillId,String sender,String receiver,BigDecimal money,Integer type,Integer transactionType,Integer outsideType){
        FilBillTransactions filBillTransactions = new FilBillTransactions();
        filBillTransactions.setFilBillId(filBillId);
        filBillTransactions.setSender(sender);
        filBillTransactions.setReceiver(receiver);
        filBillTransactions.setMoney(money);
        filBillTransactions.setType(type);
        filBillTransactions.setTransactionType(transactionType);
        filBillTransactions.setOutsideType(outsideType);
        filBillTransactions.setCreateTime(LocalDateTime.now());
        log.info("保存FIL币账单转账信息表入参：【{}】",JSON.toJSON(filBillTransactions));
        filBillTransactionsService.save(filBillTransactions);
        return filBillTransactions;
    }

    /*账单补录数据*/
    @Override
    public void backTrackingBill(String minerId, String date, BigDecimal balance, FilBillDayAgg filBillDayAgg) {
        FilBill filBill = insertFilBill(minerId,Constants.FILBILLTYPEBACKTRACKING,DateUtils.lDTStringToLocalDateTimeYMDHMS(date + " 23:59:30"));

        BigDecimal money = filBillDayAgg.getBalance().subtract(balance);
        log.info("money：【{}】",money);
        FilBillTransactions filBillTransactions = insertFilBillTransactions(filBill.getId(),minerId,Constants.BACKTRACKINGRECEIVER,
                money,Constants.TYPEOTHERFOUR,Constants.TRANSACTIONTYPEOUTSIDE,Constants.OUTSIDETYPEOUT);

        log.info("根据矿工id、日期更新所有的收入和支出");
        filBillDayAggMapper.updateFilBillDayAggByMinerIdAndDate(minerId,date,null,money,money.negate(),
                null,null,null,null,
                null,money,Constants.FILBILLDAYAGGTYPEBACKTRACKING);
    }

    /*补录账单所有的业务逻辑*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportBillBackTracking(String minerId, String todayDate, BigDecimal balance) {
        // 获取前一天的日期
        String yesterdayDate = DateUtils.lDTLocalDateTimeFormatYMD(DateUtils.lDTStringToLocalDateTimeYMDHMS(todayDate + " 00:00:00").plusDays(-1));

        FilBillBalanceDayAgg todayFilBillBalanceDayAgg = filBillBalanceDayAggService.selectFilBillBalanceDayAggByMinerIdAndDate(minerId,DateUtils.lDTStringToLocalDateYMD(todayDate));
        log.info("今天根据minerId、date查询矿工总余额表，只返回一条数据出参：【{}】",JSON.toJSON(todayFilBillBalanceDayAgg));
        if (todayFilBillBalanceDayAgg == null){
            Integer count = filBillBalanceDayAggService.insertFilBillBalanceDayAgg(minerId, DateUtils.lDTStringToLocalDateYMD(todayDate), balance);
            log.info("根据minerId、date、balance插入今天的矿工总余额表count出参：【{}】",count);
        } else {
            Integer count = filBillBalanceDayAggService.updateFilBillBalanceDayAgg(todayFilBillBalanceDayAgg.getId(), balance);
            log.info("根据id、balance修改矿工总余额表count出参：【{}】",count);
        }
        FilBillBalanceDayAgg yesterdayFilBillBalanceDayAgg = filBillBalanceDayAggService.selectFilBillBalanceDayAggByMinerIdAndDate(minerId,DateUtils.lDTStringToLocalDateYMD(yesterdayDate));
        log.info("昨天根据minerId、date查询矿工总余额表，只返回一条数据出参：【{}】",JSON.toJSON(yesterdayFilBillBalanceDayAgg));

        if (yesterdayFilBillBalanceDayAgg != null){
            BigDecimal yesterdayBalance = balance.subtract(yesterdayFilBillBalanceDayAgg.getBalance());

            FilBillDayAgg filBillDayAgg = filBillDayAggService.selectFilBillDayAggList(minerId,yesterdayDate);
            log.info("根据minerId、date查询FIL币账单消息每天汇总表出参：【{}】",JSON.toJSON(filBillDayAgg));
            if (filBillDayAgg != null && filBillDayAgg.getBalance().compareTo(yesterdayBalance) > 0){
                log.info("日统计里的余额大于mq补录数据的余额，插入一条补录账单数据minerId：【{}】,date：【{}】，yesterdayBalance：【{}】，filBillDayAgg：【{}】",
                        minerId,yesterdayDate,yesterdayBalance,JSON.toJSON(filBillDayAgg));
                this.backTrackingBill(minerId,yesterdayDate,yesterdayBalance,filBillDayAgg);
            } else if (filBillDayAgg.getBalance().compareTo(yesterdayBalance) < 0) {
                log.info("日统计里的余额小于mq补录数据的余额，不做任何操作minerId：【{}】,date：【{}】",minerId,yesterdayDate);
            } else {
                log.info("日统计里的余额等于mq补录数据的余额，不做任何操作minerId：【{}】,date：【{}】",minerId,yesterdayDate);
            }
        } else {
            log.info("矿工总余额表昨天的数据不存在，不进行补录minerId：【{}】,date：【{}】",minerId,yesterdayDate);
        }
    }

    /*分页查询日账单列表*/
    @Override
    public IPage<FilBillDayAggVO> selectFilBillDayAggPage(FilBillMonthBO filBillMonthBO) {
        LocalDateTime startDate = filBillMonthBO.getStartMonthDate();
        LocalDateTime endDate = filBillMonthBO.getEndMonthDate();
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
        String minerId = filBillMonthBO.getMinerId();
        log.info("账单月汇总入参:{}",JSON.toJSONString(filBillMonthBO));
        List<FilBillDayAgg> filBillDayAggList = filBillMapper.selectFilBillmonthAgg(minerId,filBillMonthBO.getStartMonthDate(),filBillMonthBO.getEndMonthDate());
        log.info("账单月汇总出参：【{}】",JSON.toJSON(filBillDayAggList));
        BillTotalVO billTotalVO = new BillTotalVO();
        billTotalVO = packageBillTotalVO(filBillDayAggList, billTotalVO);
        return billTotalVO;
    }

    /*账单总汇总-从矿工创建开始至今所有收入以及支出的汇总*/
    @Override
    public BillTotalVO selectFilBillAllAgg(FilBillMonthBO filBillMonthBO) {
        LocalDateTime startDate = DateUtils.stringToLocalDateTime("2019-01-01 00:00:01", DateFormatEnum.YYYY_MM_DD_HH_MM_SS);
        String minerId = filBillMonthBO.getMinerId();

        log.info("账单总汇总-从矿工创建开始至今所有收入以及支出的汇总入参minerId：【{}】,startDate：【{}】",minerId,startDate);
        List<FilBillDayAgg> filBillDayAggList = filBillMapper.selectFilBillmonthAgg(minerId,startDate,null);
        log.info("账单总汇总-从矿工创建开始至今所有收入以及支出的汇总出参：【{}】",JSON.toJSON(filBillDayAggList));

        BillTotalVO billTotalVO = new BillTotalVO();
        billTotalVO = packageBillTotalVO(filBillDayAggList, billTotalVO);

        return billTotalVO;
    }

    /**
     * 组装fil币账单汇总出参
     * @param filBillDayAggList
     * @param billTotalVO
     * @return
     */
    public BillTotalVO packageBillTotalVO(List<FilBillDayAgg> filBillDayAggList,BillTotalVO billTotalVO){
        // 收入-转账
        BigDecimal inTransferMoney = BigDecimal.ZERO;
        // 收入-区块奖励
        BigDecimal inBlockAwardMoney = BigDecimal.ZERO;
        // 支出-转账
        BigDecimal outTransferMoney = BigDecimal.ZERO;
        // 支出-矿工手续费
        BigDecimal outNodeFeeMoney = BigDecimal.ZERO;
        // 支出-燃烧手续费
        BigDecimal outBurnFeeMoney =  BigDecimal.ZERO;
        // 支出-其它
        BigDecimal outAllMoney = BigDecimal.ZERO;
        if(filBillDayAggList != null && filBillDayAggList.size() > 0 && filBillDayAggList.get(0) != null){
            FilBillDayAgg filBillDayAgg = filBillDayAggList.get(0);

            // 收入-转账
            inTransferMoney = filBillDayAgg.getInTransfer();
            inTransferMoney = inTransferMoney == null?BigDecimal.ZERO:inTransferMoney;
            log.info("收入-转账：查询账单月汇总转账收入出参：【{}】",inTransferMoney);

            // 收入-区块奖励
            inBlockAwardMoney = filBillDayAgg.getInBlockAward();
            inBlockAwardMoney = inBlockAwardMoney == null?BigDecimal.ZERO:inBlockAwardMoney;
            log.info("收入-区块奖励：查询账单月汇总区块奖励收入出参：【{}】",inBlockAwardMoney);

            // 支出-转账
            outTransferMoney = filBillDayAgg.getOutTransfer();
            outTransferMoney = outTransferMoney == null?BigDecimal.ZERO:outTransferMoney;
            log.info("支出-转账：查询账单月汇总转账支出出参：【{}】",outTransferMoney);

            // 支出-矿工手续费
            outNodeFeeMoney = filBillDayAgg.getOutNodeFee();
            outNodeFeeMoney = outNodeFeeMoney == null?BigDecimal.ZERO:outNodeFeeMoney;
            log.info("支出-矿工手续费：查询账单按照日期范围汇总矿工手续费支出出参：【{}】",outNodeFeeMoney);

            // 支出-燃烧手续费
            outBurnFeeMoney = filBillDayAgg.getOutBurnFee();
            outBurnFeeMoney = outBurnFeeMoney == null?BigDecimal.ZERO:outBurnFeeMoney;
            log.info("支出-燃烧手续费：查询账单按照日期范围汇总燃烧手续费支出出参：【{}】",outBurnFeeMoney);

            // 支出-其它
            outAllMoney = filBillDayAgg.getOutOther();
            log.info("支出-其它：查询账单按照日期范围汇总所有外部交易支出出参：【{}】",outAllMoney);
            outAllMoney = outAllMoney == null?BigDecimal.ZERO:outAllMoney;
            log.info("支出-其它：查询账单按照日期范围汇总所有外部交易支出出参2：【{}】",outAllMoney);
        }

        // 收入
        BillMethodTotalVO in = new BillMethodTotalVO();
        List<BillMethodMoneyVO> inBillMethodMoneyVOList = new ArrayList<>();

        // 收入-转账
        BillMethodMoneyVO inTransferBillMethodMoneyVO = new BillMethodMoneyVO();
        inTransferBillMethodMoneyVO.setMethod("转入");
//            BigDecimal inTransferMoney = filBillMapper.selectFilBillTransferDateAgg(1,minerId,startDate,endDate);
        inTransferBillMethodMoneyVO.setMoney(inTransferMoney);
        inBillMethodMoneyVOList.add(inTransferBillMethodMoneyVO);

        // 收入-区块奖励
        BillMethodMoneyVO inBlockAwardBillMethodMoneyVO = new BillMethodMoneyVO();
        inBlockAwardBillMethodMoneyVO.setMethod("区块奖励");
//            BigDecimal inBlockAwardMoney = filBillMapper.selectFilBillinBlockAwardDateAgg(minerId,startDate,endDate);
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
        outTransferBillMethodMoneyVO.setMethod("转出");
//            BigDecimal outTransferMoney = filBillMapper.selectFilBillTransferDateAgg(0,minerId,startDate,endDate);
        outTransferBillMethodMoneyVO.setMoney(outTransferMoney);
        outBillMethodMoneyVOList.add(outTransferBillMethodMoneyVO);

        // 支出-矿工手续费
        BillMethodMoneyVO outNodeFeeBillMethodMoneyVO = new BillMethodMoneyVO();
        outNodeFeeBillMethodMoneyVO.setMethod("存储手续费");
//            BigDecimal outNodeFeeMoney = filBillMapper.selectFilBillOutFeeDateAgg(0,minerId,startDate,endDate);
        outNodeFeeBillMethodMoneyVO.setMoney(outNodeFeeMoney);
        outBillMethodMoneyVOList.add(outNodeFeeBillMethodMoneyVO);

        // 支出-燃烧手续费
        BillMethodMoneyVO outBurnFeeBillMethodMoneyVO = new BillMethodMoneyVO();
        outBurnFeeBillMethodMoneyVO.setMethod("燃烧手续费");
//            BigDecimal outBurnFeeMoney = filBillMapper.selectFilBillOutFeeDateAgg(1,minerId,startDate,endDate);
        outBurnFeeBillMethodMoneyVO.setMoney(outBurnFeeMoney);
        outBillMethodMoneyVOList.add(outBurnFeeBillMethodMoneyVO);

        // 支出-其它
        BillMethodMoneyVO outOtherBillMethodMoneyVO = new BillMethodMoneyVO();
        outOtherBillMethodMoneyVO.setMethod("其它");
//            BigDecimal outAllMoney = filBillMapper.selectFilBillOutAllDateAgg(minerId,startDate,endDate);
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

    /*根据minerId、月份分页查询月转入、转出、区块奖励列表*/
    @Override
    public IPage<FilBillVO> selectFilBillMonthTransferPage(FilBillMonthBO filBillMonthBO) {
        LocalDateTime startDate = filBillMonthBO.getStartMonthDate();
        LocalDateTime endDate = filBillMonthBO.getEndMonthDate();
        Integer transferType = filBillMonthBO.getTransferType();
        Page<FilBillVO> page = new Page<>(filBillMonthBO.getPageNum(),filBillMonthBO.getPageSize());
        IPage<FilBillVO> filBillVOPage = filBillMapper.selectFilBillMonthTransferPage(page,filBillMonthBO.getMinerId(),startDate,endDate,transferType);
        log.info("根据minerId、月份分页查询月转入、转出、区块奖励列表出参：【{}】",JSON.toJSON(filBillVOPage));
        return filBillVOPage;
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

        // 收入-转账
        BigDecimal inTransferMoney = filBillMapper.selectFilBillTransferDateAgg(1,minerId,startDate,endDate);
        inTransferMoney = inTransferMoney == null?BigDecimal.ZERO:inTransferMoney;
        log.info("查询账单月汇总转账收入出参：【{}】",inTransferMoney);
        filBillDayAgg.setInTransfer(inTransferMoney);

        // 收入-区块奖励
        BigDecimal inBlockAwardMoney = filBillMapper.selectFilBillinBlockAwardDateAgg(minerId,startDate,endDate);
        inBlockAwardMoney = inBlockAwardMoney == null?BigDecimal.ZERO:inBlockAwardMoney;
        log.info("查询账单月汇总区块奖励收入出参：【{}】",inBlockAwardMoney);
        filBillDayAgg.setInBlockAward(inBlockAwardMoney);

        // 支出-转账
        BigDecimal outTransferMoney = filBillMapper.selectFilBillTransferDateAgg(0,minerId,startDate,endDate);
        outTransferMoney = outTransferMoney == null?BigDecimal.ZERO:outTransferMoney;
        log.info("查询账单月汇总转账支出出参：【{}】",outTransferMoney);
        filBillDayAgg.setOutTransfer(outTransferMoney);

        // 支出-矿工手续费
        BigDecimal outNodeFeeMoney = filBillMapper.selectFilBillOutFeeDateAgg(0,minerId,startDate,endDate);
        outNodeFeeMoney = outNodeFeeMoney == null?BigDecimal.ZERO:outNodeFeeMoney;
        log.info("查询账单按照日期范围汇总矿工手续费支出出参：【{}】",outNodeFeeMoney);
        filBillDayAgg.setOutNodeFee(outNodeFeeMoney);

        // 支出-燃烧手续费
        BigDecimal outBurnFeeMoney = filBillMapper.selectFilBillOutFeeDateAgg(1,minerId,startDate,endDate);
        outBurnFeeMoney = outBurnFeeMoney == null?BigDecimal.ZERO:outBurnFeeMoney;
        log.info("查询账单按照日期范围汇总燃烧手续费支出出参：【{}】",outBurnFeeMoney);
        filBillDayAgg.setOutBurnFee(outBurnFeeMoney);

        // 支出-其它
        BigDecimal outAllMoney = filBillMapper.selectFilBillOutAllDateAgg(minerId,startDate,endDate);
        log.info("查询账单按照日期范围汇总所有外部交易支出出参：【{}】",outAllMoney);
        outAllMoney = outAllMoney == null?BigDecimal.ZERO:outAllMoney;
        log.info("查询账单按照日期范围汇总所有外部交易支出出参2：【{}】",outAllMoney);
        filBillDayAgg.setOutOther(outAllMoney);

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

        // Controller和Worker和Miner这3的代码顺序不能换，换了报错
        // Controller，从redis中获取
        Map<String,String> controllerMap = redisUtil.hgetall(Constants.REDISMINERADDRESS + minerId);
        log.info("minerId：【{}】从redis中获取Controller为：【{}】",minerId,controllerMap);
        if (controllerMap != null && controllerMap.size() > 0){
            for (Map.Entry<String,String> entry:controllerMap.entrySet()){
                if (!entry.getKey().equals("Miner") && !entry.getKey().equals("Worker")){
                    FilBillSubAccountVO controllerFilBillSubAccountVO = new FilBillSubAccountVO();
                    controllerFilBillSubAccountVO.setName(entry.getKey());
                    controllerFilBillSubAccountVO.setAddress(entry.getValue());
                    filBillSubAccountVOList.add(controllerFilBillSubAccountVO);
                }
            }
        } else {
            QueryWrapper<FilMinerControlBalance> filMinerControlBalanceQueryWrapper = new QueryWrapper<>();
            FilMinerControlBalance qwFilMinerControlBalance = new FilMinerControlBalance();
            qwFilMinerControlBalance.setMinerId(minerId);
            filMinerControlBalanceQueryWrapper.setEntity(qwFilMinerControlBalance);
            List<FilMinerControlBalance> filMinerControlBalanceList = filMinerControlBalanceMapper.selectList(filMinerControlBalanceQueryWrapper);
            log.info("查询子账户Controller/Post账户余额表list出参为：【{}】",JSON.toJSON(filMinerControlBalanceList));
            if (filMinerControlBalanceList != null && filMinerControlBalanceList.size() > 0){
                for (FilMinerControlBalance filMinerControlBalance:filMinerControlBalanceList){
                    FilBillSubAccountVO controllerFilBillSubAccountVO = new FilBillSubAccountVO();
                    controllerFilBillSubAccountVO.setName(filMinerControlBalance.getName());
                    controllerFilBillSubAccountVO.setAddress(filMinerControlBalance.getAddress());
                    filBillSubAccountVOList.add(controllerFilBillSubAccountVO);
                    redisUtil.hmset(Constants.REDISMINERADDRESS + minerId,filMinerControlBalance.getName(),filMinerControlBalance.getAddress());
                    log.info("minerId：【{}】保存Controller地址到redis中：【{}】",minerId,controllerMap);
                }
            }
        }

        // Worker，从redis中获取
        String balanceWorkerAddress = redisUtil.hget(Constants.REDISMINERADDRESS + minerId,"Worker");
        log.info("minerId：【{}】从redis中获取Worker为：【{}】",minerId,balanceWorkerAddress);
        FilBillSubAccountVO workerFilBillSubAccountVO = new FilBillSubAccountVO();
        workerFilBillSubAccountVO.setName("Worker");
        if (StringUtils.isNotEmpty(balanceWorkerAddress)){
            workerFilBillSubAccountVO.setAddress(balanceWorkerAddress);
        } else {
            QueryWrapper<SysMinerInfo> workerQueryWrapper = new QueryWrapper<>();
            SysMinerInfo sysMinerInfo = new SysMinerInfo();
            sysMinerInfo.setMinerId(minerId);
            workerQueryWrapper.setEntity(sysMinerInfo);
            List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.list(workerQueryWrapper);
            log.info("查询矿工列表list出参为：【{}】",JSON.toJSON(sysMinerInfoList));
            if (sysMinerInfoList != null && sysMinerInfoList.size() > 0){
                workerFilBillSubAccountVO.setAddress(sysMinerInfoList.get(0).getBalanceWorkerAddress());
                redisUtil.hmset(Constants.REDISMINERADDRESS + minerId,"Worker",sysMinerInfoList.get(0).getBalanceWorkerAddress());
                log.info("minerId：【{}】保存Worker地址到redis中：【{}】",minerId,sysMinerInfoList.get(0).getBalanceWorkerAddress());
            }
        }
        log.info("Worker账户：【{}】",JSON.toJSON(workerFilBillSubAccountVO));
        filBillSubAccountVOList.add(workerFilBillSubAccountVO);

        // Miner
        FilBillSubAccountVO minerFilBillSubAccountVO = new FilBillSubAccountVO();
        minerFilBillSubAccountVO.setName("Miner");
        minerFilBillSubAccountVO.setAddress(minerId);
        log.info("Miner账户：【{}】",JSON.toJSON(minerFilBillSubAccountVO));
        filBillSubAccountVOList.add(minerFilBillSubAccountVO);
        String minerAddress = redisUtil.hget(Constants.REDISMINERADDRESS + minerId,"Miner");
        if (StringUtils.isEmpty(minerAddress)){
            redisUtil.hmset(Constants.REDISMINERADDRESS + minerId,"Miner",minerId);
            log.info("minerId：【{}】保存Miner地址到redis中：【{}】",minerId,minerAddress);
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
