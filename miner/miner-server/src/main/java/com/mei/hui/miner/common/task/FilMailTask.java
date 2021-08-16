package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.mei.hui.config.model.MailDO;
import com.mei.hui.miner.common.MailUtil;
import com.mei.hui.miner.feign.vo.FilMinerPowerAvailableUnitVO;
import com.mei.hui.miner.model.SysMinerInfoBO;
import com.mei.hui.miner.model.SysMinerInfoVO;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/9 19:58
 **/
@Slf4j
@Configuration
@EnableScheduling
public class FilMailTask {

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private UserFeignClient userFeignClient;

    @Value("${spring.profiles.active}")
    private String env;

    /**
    * 每天凌晨6点邮件发送矿工列表昨天0-24点的数据列表
    *
    * @description
    * @author shangbin
    * @date 2021/8/9 20:05
    * @param []
    * @return void
    * @version v1.4.1
    */
    // TODO 提交代码的时候记得修改定时器时间
//    @Scheduled(cron = "0 0 6 * * ?")
    @Scheduled(cron = "0 21 19 * * ?")
//    @Scheduled(cron = "* */5 * * * ?")
    public void selectSysMinerInfoList(){
        log.info("======================fil币定时器selectSysMinerInfoList-start===================");
        if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }
        Result<List<SysUserOut>> sysUserOutListResult = userFeignClient.findAllUser();
        log.info("获取所有有效用户出参：【{}】",JSON.toJSON(sysUserOutListResult));
        List<SysUserOut> sysUserOutList = new ArrayList<>();
        if(ErrorCode.MYB_000000.getCode().equals(sysUserOutListResult.getCode())){
            sysUserOutList = sysUserOutListResult.getData();
        }
        if (sysUserOutList == null || sysUserOutList.size() < 1){
            log.info("没有有效用户，fil币定时器selectSysMinerInfoList不执行");
            return;
        }
        for (SysUserOut sysUserOut : sysUserOutList){
            String email = sysUserOut.getEmail();
            Long userId = sysUserOut.getUserId();
            log.info("userId：【{}】,email：【{}】",userId,email);
            if (StringUtils.isEmpty(email)){
                log.info("userId：【{}】用户邮箱为空，跳过该条，执行下一条",userId);
                continue;
            }
            if (userId.equals(1L)){
                log.info("超级管理员不发，跳过该条，执行下一条");
                continue;
            }

            SysMinerInfoBO sysMinerInfoBO = new SysMinerInfoBO();
            sysMinerInfoBO.setUserId(userId);
            sysMinerInfoBO.setPageNum(1);
            sysMinerInfoBO.setPageSize(500);
            Map<String,Object> sysMinerInfoPageMap = sysMinerInfoService.findPage(sysMinerInfoBO);
            log.info("分页查询矿工信息列表出参：【{}】",JSON.toJSON(sysMinerInfoPageMap));
            List<SysMinerInfoVO> sysMinerInfoVOList = (List<SysMinerInfoVO>)sysMinerInfoPageMap.get("rows");

            if (sysMinerInfoVOList == null || sysMinerInfoVOList.size() < 1){
                log.info("用户矿工信息列表为空，跳过该条，执行下一条");
                continue;
            }

            for (SysMinerInfoVO sysMinerInfoVO : sysMinerInfoVOList){
                log.info("矿工列表出参：【{}】",JSON.toJSON(sysMinerInfoVO));
                // 有效算力页面带i的用1024换算，不带i的用1000换算，有效算力, 单位B
                BigDecimal powerAvailable = sysMinerInfoVO.getPowerAvailable();
                FilMinerPowerAvailableUnitVO filMinerPowerAvailableUnitVO = sysMinerInfoService.powerAvailableUnit(powerAvailable);
                log.info("矿工有效算力单位换算出参：【{}】",JSON.toJSON(filMinerPowerAvailableUnitVO));
                sysMinerInfoVO.setPowerAvailable(filMinerPowerAvailableUnitVO.getPowerAvailable());
                sysMinerInfoVO.setPowerAvailableUnit(filMinerPowerAvailableUnitVO.getPowerAvailableUnit());

                // 算力增速
                BigDecimal powerIncreasePerDay = sysMinerInfoVO.getPowerIncreasePerDay();
                FilMinerPowerAvailableUnitVO filMinerPowerIncreasePerDayUnitVO = sysMinerInfoService.powerAvailableUnit(powerIncreasePerDay);
                log.info("矿工算力增速单位换算出参：【{}】",JSON.toJSON(filMinerPowerIncreasePerDayUnitVO));
                sysMinerInfoVO.setPowerIncreasePerDay(filMinerPowerIncreasePerDayUnitVO.getPowerAvailable());
                sysMinerInfoVO.setPowerIncreasePerDayUnit(filMinerPowerIncreasePerDayUnitVO.getPowerAvailableUnit());
            }
            log.info("矿工信息列表：【{}】",JSON.toJSON(sysMinerInfoVOList));

            Map<String,Object> map = new HashMap<>();
            map.put("list",sysMinerInfoVOList);

            String yesterDayDateYmd = DateUtils.getYesterDayDateYmd();
            String title = "数据统计的日期为" + yesterDayDateYmd.substring(0,4) + "年" + yesterDayDateYmd.substring(5,7) + "月" + yesterDayDateYmd.substring(8,10) + "日23时59分";
            MailDO mail = new MailDO();
            mail.setContent("矿工列表");
            mail.setEmail(email);
            mail.setTitle(title);
            mail.setAttachment(map);
            log.info(title);
            MailUtil.sendTemplateMail(mail);
            log.info("userId：【{}】，email：【{}】，map：【{}】发送完成",userId,email,JSON.toJSON(map));
        }
        log.info("======================fil币定时器selectSysMinerInfoList-end===================");
    }


}
