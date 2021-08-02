package com.mei.hui.miner.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.entity.FilBill;
import com.mei.hui.miner.entity.FilBillParams;
import com.mei.hui.miner.entity.FilBlockAward;
import com.mei.hui.miner.feign.vo.FilBillReportBO;
import com.mei.hui.miner.feign.vo.FilBlockAwardReportBO;
import com.mei.hui.miner.service.FilBillParamsService;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.FilBlockAwardService;
import com.rabbitmq.client.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class FilRabbitMQListener {

    @Autowired
    private FilBillService filBillService;
    @Autowired
    private FilBlockAwardService filBlockAwardService;
    @Autowired
    private FilBillParamsService filBillParamsService;


    /**
     * 上报FIL币账单
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = {"fil.bill.queue"})//从哪个队列取消息
    @RabbitHandler
    public void processBill(Channel channel, Message message) throws IOException {
        byte[] body = message.getBody();
        String messageStr = new String(body,"UTF-8");
        log.info("FIL币账单rabbitmq上报入参【{}】：" + messageStr);
        List<FilBillReportBO> filBillReportBOList = JSONObject.parseArray(messageStr,FilBillReportBO.class);
        log.info("FIL币账单rabbitmq上报入参转成list结果：【{}】",filBillReportBOList);
//            Integer count = filBillService.reportFilBill(filBillReportBOList);
        for (FilBillReportBO filBillReportBO : filBillReportBOList){
            log.info("filBillReportBO入参：【{}】",filBillReportBO);
            QueryWrapper<FilBill> queryWrapper = new QueryWrapper<>();
            FilBill dbFilBill = new FilBill();
            dbFilBill.setMessageId(filBillReportBO.getCid());
            queryWrapper.setEntity(dbFilBill);
            List<FilBill> filBillList = filBillService.list(queryWrapper);
            log.info("查询数据库里该条消息MessageId：【{}】是否存在出参：【{}】",filBillReportBO.getCid(),JSON.toJSON(filBillList));
            if (filBillList != null && filBillList.size() > 0){
                continue;
            }

            FilBill filBill = new FilBill();
            BeanUtils.copyProperties(filBillReportBO,filBill);
            filBill.setMinerId(filBillReportBO.getMiner());
            filBill.setMessageId(filBillReportBO.getCid());
            filBill.setBlockHeight(filBillReportBO.getHeight());
            filBill.setSender(filBillReportBO.getFrom());
            filBill.setReceiver(filBillReportBO.getTo());
            filBill.setMoney(filBillReportBO.getValue());
            filBill.setDateTime(LocalDateTime.ofEpochSecond(filBillReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8)));
            filBill.setCreateTime(LocalDateTime.now());
            try {
                log.info("保存FIL币账单入参：【{}】",filBill);
                filBillService.save(filBill);

                String params = filBillReportBO.getParams();
                if (StringUtils.isNotEmpty(params)){
                    FilBillParams filBillParams = new FilBillParams();
                    filBillParams.setFilBillId(filBill.getId());
                    filBillParams.setParams(filBillReportBO.getParams());
                    log.info("保存FIL币账单参数入参：【{}】",filBillParams);
                    filBillParamsService.save(filBillParams);
                }
                // 对于每个Channel来说，每个消息都会有一个DeliveryTag，一般用接收消息的顺序(index)来表示，一条消息就为1
                log.info("确认消息");
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception e) {
                log.info("接收到消息之后的处理发生异常：【{}】", e);
                // 报错后直接拒绝，并设置requeue属性为false，这样被拒绝的消息就不会重新回到原始队列中而是转发到死信交换机
                log.info("异常后消息进入死信队列");
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }
        }

    }

    /**
     * 上报fil币区块奖励详情
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitListener(queues = {"fil.reward.queue"})//从哪个队列取消息
    @RabbitHandler
    public void processReward(Channel channel, Message message) throws IOException {
        byte[] body = message.getBody();
        String messageStr = new String(body,"UTF-8");
        log.info("FIL币区块奖励详情rabbitmq上报入参【{}】：" + messageStr);
        try {
            FilBlockAwardReportBO filBlockAwardReportBO = JSONObject.parseObject(messageStr,FilBlockAwardReportBO.class);
            FilBlockAward filBlockAward = new FilBlockAward();
            BeanUtils.copyProperties(filBlockAwardReportBO,filBlockAward);
            filBlockAward.setMinerId(filBlockAwardReportBO.getMiner());
            filBlockAward.setMessageId(filBlockAwardReportBO.getCid());
            filBlockAward.setDateTime(LocalDateTime.ofEpochSecond(filBlockAwardReportBO.getTimestamp(), 0, ZoneOffset.ofHours(8)));
            filBlockAward.setCreateTime(LocalDateTime.now());
            log.info("保存FIL币区块奖励详情入参：【{}】",filBlockAward);
            filBlockAwardService.save(filBlockAward);
            // 对于每个Channel来说，每个消息都会有一个DeliveryTag，一般用接收消息的顺序(index)来表示，一条消息就为1
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.info("接收到消息之后的处理发生异常：【{}】", e);
            // 报错后直接拒绝，并设置requeue属性为false，这样被拒绝的消息就不会重新回到原始队列中而是转发到死信交换机
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }

    }


}

