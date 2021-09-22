package com.mei.hui.user.SystemController;

import com.alibaba.fastjson.JSON;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.feign.feignClient.MinerFeignClient;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.model.CheckConnectionStatusVO;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * mysql、redis、mq、矿工模块宕机运维报警功能
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/9/22 11:42
 **/
@Api(tags = "宕机运维报警功能")
@Slf4j
@RequestMapping("/check")
@RestController
public class CheckConnectionController {

    @Autowired
    private MinerFeignClient minerFeignClient;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    @ApiOperation("mysql、redis、mq、矿工模块宕机运维报警功能")
    @GetMapping("/CheckConnection")
    public Result<CheckConnectionStatusVO> checkConnection(){
        CheckConnectionStatusVO checkConnectionStatusVO = new CheckConnectionStatusVO();
        checkConnectionStatusVO.setDateTime(DateUtils.lDTLocalDateTimeNow());

        // miner
        try {
            Result<Integer> minerResult = minerFeignClient.CheckConnectionListAll();
            if (minerResult != null){
                log.info("宕机运维报警功能-查询矿工列表的数量结果:{}", JSON.toJSONString(minerResult));
                if(ErrorCode.MYB_000000.getCode().equals(minerResult.getCode())){
                    checkConnectionStatusVO.setMiner(Constants.CHECKCONNECTIONSTATUSON);
                } else {
                    log.info("宕机运维报警功能-miner，返回状态码不正确");
                    checkConnectionStatusVO.setMiner(Constants.CHECKCONNECTIONSTATUSOFF);
                }
            } else {
                log.info("宕机运维报警功能-miner，miner服务没有返回值");
                checkConnectionStatusVO.setMiner(Constants.CHECKCONNECTIONSTATUSOFF);
            }
        } catch (Exception e){
            log.info("宕机运维报警功能-miner，miner服务发生异常");
            checkConnectionStatusVO.setMiner(Constants.CHECKCONNECTIONSTATUSOFF);
        }

        // redis
        String redisKey = Constants.CHECKCONNECTIONSTATUSREDISKEY;
        try {
            redisUtil.set(redisKey,"1");
            String redisValue = redisUtil.get(redisKey);
            log.info("宕机运维报警功能-redis：【{}】",redisValue);
            if (StringUtils.isNotEmpty(redisValue)){
                checkConnectionStatusVO.setRedis(Constants.CHECKCONNECTIONSTATUSON);
            } else {
                log.info("宕机运维报警功能-redis");
                checkConnectionStatusVO.setRedis(Constants.CHECKCONNECTIONSTATUSOFF);
            }
        } catch (Exception e){
            log.info("宕机运维报警功能-redis，发生异常");
            checkConnectionStatusVO.setRedis(Constants.CHECKCONNECTIONSTATUSOFF);
        }

        // mq
        String sendMsg = "1";
        //第一个参数为队列名称,第二个参数为要发送的消息对象,这里传的是一个字符串
        try {
            rabbitTemplate.convertAndSend(Constants.CHECKCONNECTIONSTATUSMQQUEUE, sendMsg);
            log.info("mq发送消息:{}",sendMsg);
            checkConnectionStatusVO.setMq(Constants.CHECKCONNECTIONSTATUSON);
        } catch (Exception e){
            log.info("宕机运维报警功能-mq，发生异常");
            checkConnectionStatusVO.setMq(Constants.CHECKCONNECTIONSTATUSOFF);
        }

        log.info("宕机运维报警功能出参：【{}】",checkConnectionStatusVO);
        return Result.success(checkConnectionStatusVO);
    }

}
