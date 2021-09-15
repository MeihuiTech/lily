package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillDayAgg;
import com.mei.hui.miner.entity.FilBillTransactions;
import com.mei.hui.miner.feign.vo.FilBillDayAggArgsVO;
import com.mei.hui.miner.feign.vo.FilBillReportBO;
import com.mei.hui.miner.feign.vo.FilBillReportListBO;
import com.mei.hui.miner.service.FilBillDayAggService;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.FilBillTransactionsService;
import com.mei.hui.miner.service.FilBlockAwardService;
import com.mei.hui.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/9/10 10:37
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication.class)
@Slf4j
public class FilRabbitMQTest {

    @Autowired
    private FilBillService filBillService;
    @Autowired
    private FilBlockAwardService filBlockAwardService;
    @Autowired
    private FilBillTransactionsService filBillTransactionsService;
    @Autowired
    private FilBillDayAggService filBillDayAggService;


    @Test
    public void filBillMQ(){
        String messageStr = "{\"miner\":\"f01016365\",\"messages\":[{\"cid\":\"bafy2bzacedwjmonjtgwpa34gz4vr7kexxmm3y6gl7eppynhwkct3kxnxfy2h2\",\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f01016365\",\"sizeBytes\":136,\"nonce\":1,\"value\":\"0\",\"gasFeeCap\":\"102140\",\"gasPremium\":\"99737\",\"gasLimit\":\"2259007\",\"height\":807620,\"stateRoot\":\"bafy2bzacec5m46fg5omt75w4dbdjzb6fdo5t2noj5auadwvobdo5k2lucdisg\",\"exitCode\":0,\"gasUsed\":1867006,\"parentBaseFee\":\"218\",\"baseFeeBurn\":\"407007308\",\"overEstimationBurn\":\"9396890\",\"minerPenalty\":\"0\",\"minerTip\":\"225306581159\",\"refund\":\"5011989623\",\"gasRefund\":348896,\"gasBurned\":43105,\"miner\":\"f01016365\",\"method\":\"ChangeWorkerAddress\",\"timestamp\":1622535000,\"transaction\":[{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f099\",\"value\":\"416404198\",\"type\":\"Burn Fee\"},{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f022373\",\"value\":\"225306581159\",\"type\":\"Node Fee\"}]}],\"balance\":\"0\",\"date\":\"2021-06-01\",\"firstTipSet\":false}";
        log.info("FIL币账单rabbitmq上报入参【{}】：" , messageStr);
        FilBillReportListBO filBillReportListBO = JSONObject.parseObject(messageStr,FilBillReportListBO.class);
        List<FilBill> filBillList = new ArrayList<>();
        FilBillDayAggArgsVO filBillDayAggArgsVO = new FilBillDayAggArgsVO();
        List<FilBillTransactions> allFilBillTransactionsList = new ArrayList<>();
        List<FilBillReportBO> filBillReportBOList = filBillReportListBO.getMessages();
        log.info("FIL币账单rabbitmq上报入参转成list结果：【{}】",JSON.toJSON(filBillReportBOList));
        if (filBillReportBOList != null && filBillReportBOList.size() > 0){
            for (FilBillReportBO filBillReportBO : filBillReportBOList){
                List<FilBillTransactions> filBillTransactionsList = new ArrayList<>();
//                log.info("filBillReportBO入参：【{}】",JSON.toJSON(filBillReportBO));
                QueryWrapper<FilBill> queryWrapper = new QueryWrapper<>();
                FilBill dbFilBill = new FilBill();
                String cid = filBillReportBO.getCid();
                dbFilBill.setCid(cid);
                queryWrapper.setEntity(dbFilBill);
                List<FilBill> dbFilBillList = filBillService.list(queryWrapper);
                log.info("查询数据库里该条消息MessageId：【{}】是否存在出参：【{}】",cid,JSON.toJSON(dbFilBillList));
                if (dbFilBillList != null && dbFilBillList.size() > 0){
                    log.info("该条数据MessageId：【{}】数据库中已经存在，不插入，并且确认消息",cid);
                    continue;
                }

                filBillService.reportBillMq(filBillReportBO,filBillList, filBillTransactionsList,filBillDayAggArgsVO);
                log.info("保存上报FIL币账单出参：filBillList：【{}】，filBillTransactionsList：【{}】，filBillDayAggArgsVO：【{}】",JSON.toJSON(filBillList),
                        JSON.toJSON(filBillTransactionsList),JSON.toJSON(filBillDayAggArgsVO));

                allFilBillTransactionsList.addAll(filBillTransactionsList);
            }
        }

        // 判断是否补录
        /*BigDecimal balance = filBillReportListBO.getBalance();
        if (filBillReportListBO.isFirstTipSet()){
            String minerId = filBillReportListBO.getMiner();
            String date = filBillReportListBO.getDate();
            // 获取前一天的日期
            date = DateUtils.lDTLocalDateTimeFormatYMD(DateUtils.lDTStringToLocalDateTimeYMDHMS(date + " 00:00:00").plusDays(-1));
            FilBillDayAgg filBillDayAgg = filBillDayAggService.selectFilBillDayAggList(minerId,date);
            log.info("根据minerId、date查询FIL币账单消息每天汇总表出参：【{}】",JSON.toJSON(filBillDayAgg));
            if (filBillDayAgg != null && filBillDayAgg.getBalance().compareTo(balance) > 0){
                log.info("日统计里的余额大于mq补录数据的余额，插入一条补录账单数据minerId：【{}】,date：【{}】，balance：【{}】，filBillDayAgg：【{}】",
                        minerId,date,balance,JSON.toJSON(filBillDayAgg));
                filBillService.backTrackingBill(minerId,date,balance,filBillDayAgg);
            } else if (filBillDayAgg.getBalance().compareTo(balance) < 0) {
                log.info("日统计里的余额小于mq补录数据的余额，不做任何操作");
            } else {
                log.info("日统计里的余额等于mq补录数据的余额，不做任何操作");
            }
        }

        if(filBillList != null && filBillList.size() > 0 && allFilBillTransactionsList != null && allFilBillTransactionsList.size() > 0){
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(filBillReportBOList.get(0).getTimestamp(), 0, ZoneOffset.ofHours(8));
            log.info("批量保存FIL币账单消息详情表、FIL币账单转账信息表，实时计算FIL币账单消息每天汇总表数据minerId：【{}】，dateTime：【{}】，filBillList：【{}】，" +
                            "allFilBillTransactionsList：【{}】，filBillDayAggArgsVO：【{}】",filBillReportBOList.get(0).getMiner(),dateTime,JSON.toJSON(filBillList),
                    JSON.toJSON(allFilBillTransactionsList),JSON.toJSON(filBillDayAggArgsVO));
            filBillService.saveBatchReportBillMq(filBillReportBOList.get(0).getMiner(),dateTime,filBillList,allFilBillTransactionsList,filBillDayAggArgsVO);
        }*/
    }



}
