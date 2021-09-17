package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.common.Constants;
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
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private RedisUtil redisUtil;


    @Test
    public void filBillMQ(){
        String messageStr = "{\"miner\":\"f01016365\",\"messages\":[{\"cid\":\"\",\"from\":\"f02\",\"to\":\"f01016365\",\"sizeBytes\":0,\"nonce\":0,\"value\":\"25433472551625396653\",\"gasFeeCap\":\"0\",\"gasPremium\":\"0\",\"gasLimit\":\"0\",\"height\":821498,\"stateRoot\":\"bafy2bzacebyon67z6kn7c5xgc4itqba5n6ysjsajpw5payzy7hr2tgqo3tnta\",\"exitCode\":0,\"gasUsed\":0,\"parentBaseFee\":\"0\",\"baseFeeBurn\":\"0\",\"overEstimationBurn\":\"0\",\"minerPenalty\":\"0\",\"minerTip\":\"0\",\"refund\":\"0\",\"gasRefund\":0,\"gasBurned\":0,\"miner\":\"f01016365\",\"method\":\"AwardBlockReward\",\"timestamp\":1631785706,\"transaction\":[{\"from\":\"f02\",\"to\":\"f01016365\",\"value\":\"25433472551625396653\",\"type\":\"AwardBlockReward\"}]}],\"balance\":\"0\",\"date\":\"2021-09-16\",\"firstTipSet\":false}";
        log.info("FIL币账单rabbitmq上报入参【{}】：" , messageStr);
        FilBillReportListBO filBillReportListBO = JSONObject.parseObject(messageStr,FilBillReportListBO.class);
        List<FilBill> filBillList = new ArrayList<>();
        FilBillDayAggArgsVO filBillDayAggArgsVO = new FilBillDayAggArgsVO();
        List<FilBillTransactions> allFilBillTransactionsList = new ArrayList<>();
        List<FilBillReportBO> filBillReportBOList = filBillReportListBO.getMessages();
        log.info("FIL币账单rabbitmq上报入参转成list结果：【{}】",JSON.toJSON(filBillReportBOList));
        if (filBillReportBOList != null && filBillReportBOList.size() > 0){
            for (FilBillReportBO filBillReportBO : filBillReportBOList){
                // 判断该天账单矿工总余额表是否补录过，如果补录过，则不插入，跳过该条数据，如果没有不补录过，则正常走下面的逻辑
                String minerId = filBillReportBO.getMiner();
                String mqDateYMD = DateUtils.lDTLocalDateTimeFormatYMD(LocalDateTime.ofEpochSecond(filBillReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8)));
                String redisKey = String.format(Constants.FILBILLBALANCEDAYAGGKEY,minerId,mqDateYMD);
                String backTrackingBillRedisValue = redisUtil.get(redisKey);
                log.info("从redis里查该天账单矿工总余额表是否补录过redisKey：【{}】，backTrackingBillRedisValue：【{}】",redisKey,backTrackingBillRedisValue);
                if (StringUtils.isNotEmpty(backTrackingBillRedisValue)){
                    log.info("从redis里查该天账单矿工总余额表里已经补录过，跳过该条数据，继续下一条账单redisKey：【{}】",redisKey);
                    continue;
                }

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
        if (filBillReportListBO.isFirstTipSet()){
            BigDecimal balance = filBillReportListBO.getBalance();
            String minerId = filBillReportListBO.getMiner();
            String todayDate = filBillReportListBO.getDate();
            filBillService.reportBillBackTracking(minerId,todayDate,balance);
        }

        if(filBillList != null && filBillList.size() > 0 && allFilBillTransactionsList != null && allFilBillTransactionsList.size() > 0){
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(filBillReportBOList.get(0).getTimestamp(), 0, ZoneOffset.ofHours(8));
            log.info("批量保存FIL币账单消息详情表、FIL币账单转账信息表，实时计算FIL币账单消息每天汇总表数据minerId：【{}】，dateTime：【{}】，filBillList：【{}】，" +
                            "allFilBillTransactionsList：【{}】，filBillDayAggArgsVO：【{}】",filBillReportBOList.get(0).getMiner(),dateTime,JSON.toJSON(filBillList),
                    JSON.toJSON(allFilBillTransactionsList),JSON.toJSON(filBillDayAggArgsVO));
            filBillService.saveBatchReportBillMq(filBillReportBOList.get(0).getMiner(),dateTime,filBillList,allFilBillTransactionsList,filBillDayAggArgsVO);
        }

    }



}
