package com.mei.hui.miner.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillTransactions;
import com.mei.hui.miner.entity.FilBlockAward;
import com.mei.hui.miner.feign.vo.FilBillDayAggArgsVO;
import com.mei.hui.miner.feign.vo.FilBillReportBO;
import com.mei.hui.miner.feign.vo.FilBlockAwardReportBO;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.FilBlockAwardService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FilRabbitMQListener {

    @Autowired
    private FilBillService filBillService;
    @Autowired
    private FilBlockAwardService filBlockAwardService;



    /**
     * 上报FIL币账单
     * @param channel
     * @param message
     * @throws IOException
     */
    // TODO 开发环境注释，测试环境暂时注释，正式环境发版的时候不要注释
    @RabbitListener(queues = {"fil.bill.queue"})//从哪个队列取消息
    @RabbitHandler
    public void reportBillMq(Channel channel, Message message) throws IOException {
        try {
            byte[] body = message.getBody();
            String messageStr = new String(body,"UTF-8");
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

            if(filBillList != null && filBillList.size() > 0 && allFilBillTransactionsList != null && allFilBillTransactionsList.size() > 0){
                LocalDateTime dateTime = LocalDateTime.ofEpochSecond(filBillReportBOList.get(0).getTimestamp(), 0, ZoneOffset.ofHours(8));
                log.info("批量保存FIL币账单消息详情表、FIL币账单转账信息表，实时计算FIL币账单消息每天汇总表数据minerId：【{}】，dateTime：【{}】，filBillList：【{}】，" +
                        "allFilBillTransactionsList：【{}】，filBillDayAggArgsVO：【{}】",filBillReportBOList.get(0).getMiner(),dateTime,JSON.toJSON(filBillList),
                        JSON.toJSON(allFilBillTransactionsList),JSON.toJSON(filBillDayAggArgsVO));
                filBillService.saveBatchReportBillMq(filBillReportBOList.get(0).getMiner(),dateTime,filBillList,allFilBillTransactionsList,filBillDayAggArgsVO);
            }

            // 对于每个Channel来说，每个消息都会有一个DeliveryTag，一般用接收消息的顺序(index)来表示，一条消息就为1
            log.info("确认消息");
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("接收到FIL币账单异常,消息进入死信队列", e);
            // 报错后直接拒绝，并设置requeue属性为false，这样被拒绝的消息就不会重新回到原始队列中而是转发到死信交换机
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    /**
     * 上报fil币区块奖励详情
     * @param channel
     * @param message
     * @throws IOException
     */
    // TODO 开发环境注释，测试环境暂时注释，正式环境发版的时候不要注释
    @RabbitListener(queues = {"fil.reward.queue"})//从哪个队列取消息
    @RabbitHandler
    public void reportFilBlockAwardMq(Channel channel, Message message) throws IOException {
        byte[] body = message.getBody();
        String messageStr = new String(body,"UTF-8");
        log.info("FIL币区块奖励详情rabbitmq上报入参【{}】：" , messageStr);
        try {
            FilBlockAwardReportBO filBlockAwardReportBO = JSONObject.parseObject(messageStr,FilBlockAwardReportBO.class);
            log.info("FilBlockAwardReportBO入参：【{}】",filBlockAwardReportBO);
            QueryWrapper<FilBlockAward> queryWrapper = new QueryWrapper<>();
            FilBlockAward dbFilBlockAward = new FilBlockAward();
            String cid = filBlockAwardReportBO.getCid();
            dbFilBlockAward.setCid(cid);
            queryWrapper.setEntity(dbFilBlockAward);
            List<FilBlockAward> filBlockAwardList = filBlockAwardService.list(queryWrapper);
            log.info("查询数据库里该条消息MessageId：【{}】是否存在出参：【{}】",cid,JSON.toJSON(filBlockAwardList));
            if (filBlockAwardList != null && filBlockAwardList.size() > 0){
                log.info("该条数据MessageId：【{}】数据库中已经存在，不插入，并且确认消息",cid);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }

            FilBillDayAggArgsVO filBillDayAggArgsVO = new FilBillDayAggArgsVO();
            LocalDateTime dateTime = LocalDateTime.ofEpochSecond(filBlockAwardReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8));

            log.info("批量保存FIL币账单消息详情表、FIL币账单转账信息表，实时计算FIL币账单消息每天汇总表数据minerId：【{}】，dateTime：【{}】,filBlockAwardReportBO：【{}】" +
                            "，filBillDayAggArgsVO：【{}】",filBlockAwardReportBO.getMiner(),dateTime,JSON.toJSON(filBlockAwardReportBO),JSON.toJSON(filBillDayAggArgsVO));
            filBlockAwardService.reportFilBlockAwardMq(filBlockAwardReportBO.getMiner(),dateTime, filBlockAwardReportBO, filBillDayAggArgsVO);

            // 对于每个Channel来说，每个消息都会有一个DeliveryTag，一般用接收消息的顺序(index)来表示，一条消息就为1
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("接收FIL币区块奖励异常,消息进入死信队列", e);
            // 报错后直接拒绝，并设置requeue属性为false，这样被拒绝的消息就不会重新回到原始队列中而是转发到死信交换机
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }

    }


}

