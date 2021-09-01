package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.miner.entity.FilBillDayAgg;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.FilBillDayAggMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * FIL币账单定时器
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/8/19 20:17
 **/
@Slf4j
@Configuration
@EnableScheduling
public class FilBillTask {

    @Autowired
    private FilBillService filBillService;
    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;
    @Autowired
    private FilBillDayAggMapper filBillDayAggMapper;

    @Value("${spring.profiles.active}")
    private String env;



    /**
     * fil币账单按天聚合，每天晚上1点0分0秒执行前一天的数据
     */
    // TODO 提交代码时记得修改
    @Scheduled(cron = "0 0 1 */1 * ?")
//    @Scheduled(cron = "* */5 * * * ?")
    public void insertFilBillDayAggTask() {
        log.info("======================fil币定时器insertFilBillDayAggTask-start===================");
        // TODO 提交代码时记得放开注释
        if ("dev".equals(env)) {
            log.info("开发环境,不执行");
            return;
        }

        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoMapper.selectList(null);
        log.info("查询所有矿工信息列表：【{}】",JSON.toJSON(sysMinerInfoList));
        log.info("矿工数量：【{}】",sysMinerInfoList.size());
        Integer insertCountAll = 0;

        // 获取昨天的开始时间
        String startDate = DateUtils.getBeginYesterdayDate().toString().substring(0,19);
        // 获取昨天的结束时间
        String endDate = DateUtils.getEndYesterdayDate().toString().substring(0,19);
        // 昨天的日期
        LocalDate date = LocalDate.now().plusDays(-1);
        // 下面代码是测试用的，不要放开注释
//        String startDate = "2021-06-10 00:00:00";
//        String endDate = "2021-06-10 23:59:59";
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate date = LocalDate.parse("2021-06-10", dateTimeFormatter);

        for (SysMinerInfo sysMinerInfo:sysMinerInfoList){
            String minerId = sysMinerInfo.getMinerId();
            try {
                QueryWrapper<FilBillDayAgg> queryWrapper = new QueryWrapper<>();
                FilBillDayAgg filBillDayAgg = new FilBillDayAgg();
                filBillDayAgg.setMinerId(minerId);
                filBillDayAgg.setDate(date);
                queryWrapper.setEntity(filBillDayAgg);
                List<FilBillDayAgg> filBillDayAggList = filBillDayAggMapper.selectList(queryWrapper);
                log.info("根据矿工minerId：【{}】，日期Date：【{}】查询FIL币账单消息每天汇总表出参：【{}】",minerId,filBillDayAgg.getDate(),JSON.toJSON(filBillDayAggList));
                if (filBillDayAggList != null && filBillDayAggList.size() > 0){
                    log.info("该矿工minerId：【{}】，日期Date：【{}】数据已存在，跳过",minerId,filBillDayAgg.getDate());
                    continue;
                }

                log.info("新增FIL币账单消息每天汇总表入参minerId：【{}】，startDate：【{}】，endDate：【{}】，date：【{}】",minerId,startDate,endDate,date);
                Integer insertCount = filBillService.insertFilBillDayAgg(minerId,startDate,endDate,date);
                insertCountAll += insertCount;
            } catch (Exception e){
                log.info("新增FIL币账单消息每天汇总表异常：minerId：【{}】，startDate：【{}】，endDate：【{}】，date：【{}】",minerId,startDate,endDate,date);
                log.info("异常信息：",e);
            }
        }
        log.info("新增FIL币账单消息每天汇总表插入的总条数为：【{}】",insertCountAll);

        log.info("======================fil币定时器insertFilBillDayAggTask-end===================");
    }


}
