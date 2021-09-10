package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillTransactions;
import com.mei.hui.miner.feign.vo.FilBillDayAggArgsVO;
import com.mei.hui.miner.feign.vo.FilBillReportBO;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.FilBillTransactionsService;
import com.mei.hui.miner.service.FilBlockAwardService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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


    @Test
    public void filBillMQ(){
        String messageStr = "[{\"cid\":\"bafy2bzaceasw6ur4fpt35xhwcoug7tvfbfbqeulvgohcw4suwsf47wgkvd4ci\",\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f01016365\",\"sizeBytes\":2020,\"nonce\":116068,\"value\":\"318815919044178336\",\"gasFeeCap\":\"3054027005\",\"gasPremium\":\"99670\",\"gasLimit\":\"53682271\",\"height\":1097848,\"stateRoot\":\"bafy2bzacedste2c6gmkiihrppx4gv4vk53yadn2qg5sfb5stgf5nbi2qs6i4q\",\"exitCode\":0,\"gasUsed\":46012734,\"parentBaseFee\":\"313950338\",\"baseFeeBurn\":\"14445713391604092\",\"overEstimationBurn\":\"160562679512326\",\"minerPenalty\":\"0\",\"minerTip\":\"5350511950570\",\"refund\":\"149335478740661367\",\"gasRefund\":7158110,\"gasBurned\":511427,\"miner\":\"f01016365\",\"method\":\"ProveCommitSector\",\"timestamp\":1631241840,\"transaction\":[{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f01016365\",\"value\":\"318815919044178336\",\"type\":\"Transfer\"},{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f099\",\"value\":\"14606276071116418\",\"type\":\"Burn Fee\"},{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f0145060\",\"value\":\"5350511950570\",\"type\":\"Node Fee\"}]},{\"cid\":\"bafy2bzacedbdwbhx5sukyc3rxioxw7sfrlgaiyxa5wkvwbpxebmatery6msyu\",\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f01016365\",\"sizeBytes\":156,\"nonce\":116069,\"value\":\"40807804398330598\",\"gasFeeCap\":\"3054026718\",\"gasPremium\":\"99383\",\"gasLimit\":\"15948368\",\"height\":1097848,\"stateRoot\":\"bafy2bzacedste2c6gmkiihrppx4gv4vk53yadn2qg5sfb5stgf5nbi2qs6i4q\",\"exitCode\":0,\"gasUsed\":12819795,\"parentBaseFee\":\"313950338\",\"baseFeeBurn\":\"4024778973340710\",\"overEstimationBurn\":\"141480777968686\",\"minerPenalty\":\"0\",\"minerTip\":\"1584996656944\",\"refund\":\"44538897232529884\",\"gasRefund\":2677926,\"gasBurned\":450647,\"miner\":\"f01016365\",\"method\":\"PreCommitSector\",\"timestamp\":1631241840,\"transaction\":[{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f01016365\",\"value\":\"40807804398330598\",\"type\":\"Transfer\"},{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f099\",\"value\":\"4166259751309396\",\"type\":\"Burn Fee\"},{\"from\":\"f3wuv4uczcwbpymvzks3qrjb2vlgzpdroba7te4g3brn6ofotthfaomexr7by5qsivn5eef4c2n2bjfj3umz6a\",\"to\":\"f0145060\",\"value\":\"1584996656944\",\"type\":\"Node Fee\"}]}]";
        log.info("FIL币账单rabbitmq上报入参【{}】：" , messageStr);
        List<FilBillReportBO> filBillReportBOList = JSONObject.parseArray(messageStr,FilBillReportBO.class);
        log.info("FIL币账单rabbitmq上报入参转成list结果：【{}】",JSON.toJSON(filBillReportBOList));
        List<FilBill> filBillList = new ArrayList<>();
        FilBillDayAggArgsVO filBillDayAggArgsVO = new FilBillDayAggArgsVO();
        List<FilBillTransactions> allFilBillTransactionsList = new ArrayList<>();
        for (FilBillReportBO filBillReportBO : filBillReportBOList){
            List<FilBillTransactions> filBillTransactionsList = new ArrayList<>();
            log.info("filBillReportBO入参：【{}】",JSON.toJSON(filBillReportBO));
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
            allFilBillTransactionsList.addAll(filBillTransactionsList);
        }

//            log.info("allFilBillTransactionsList转set之前的值：【{}】",JSON.toJSON(allFilBillTransactionsList));
//            Set<FilBillTransactions> filBillTransactionsSet = new HashSet<FilBillTransactions>(allFilBillTransactionsList);
//            allFilBillTransactionsList = new ArrayList<FilBillTransactions>(filBillTransactionsSet);
//            log.info("allFilBillTransactionsList转set之前后的值：【{}】",JSON.toJSON(allFilBillTransactionsList));

        if(filBillList != null && filBillList.size() > 0 && allFilBillTransactionsList != null && allFilBillTransactionsList.size() > 0){
            log.info("批量保存FIL币账单消息详情表入参：【{}】",JSON.toJSON(filBillList));
            filBillService.saveBatch(filBillList);
            log.info("批量保存FIL币账单转账信息表入参：【{}】",JSON.toJSON(allFilBillTransactionsList));
            filBillTransactionsService.saveBatch(allFilBillTransactionsList);

            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(filBillReportBOList.get(0).getTimestamp(), 0, ZoneOffset.ofHours(8));
            filBillService.insertOrUpdateFilBillDayAggByMinerIdAndDateAll(filBillReportBOList.get(0).getMiner(),dateTime,filBillDayAggArgsVO);
        }
    }



}
