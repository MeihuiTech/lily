package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.entity.QiniuOneDayAgg;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.entity.SysAggPowerDaily;
import com.mei.hui.miner.service.*;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * chia币定时任务
 */
@Configuration
@EnableScheduling
@Slf4j
public class QiniuOneDayAggTask {

    @Autowired
    private QiniuOneDayAggService qiniuOneDayAggService;

    @Autowired
    private RuoYiConfig ruoYiConfig;
    @Autowired
    private DiskService diskService;

    /**
     * 每天凌晨1点获取七牛云累计存储使用量
     */
    @Scheduled(cron = "0 5 0 * * ?")
    public void run() {
        log.info("======================QiniuOneDayAggTask-start===================");
        BigDecimal size = new BigDecimal("0");
        long timestamp = LocalDate.now().minusDays(1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().getEpochSecond();
        try {
            String metric = "avg_over_time(kodo_qbs_blkmaster_physical_space_capacity_bytes[1d]) - avg_over_time(kodo_qbs_blkmaster_physical_space_avail_bytes[1d])";
            String url = ruoYiConfig.getQiNiuPrometheusUrl()+"/api/v1/query?query="+ URLEncoder.encode(metric,"UTF-8")+"&time="+timestamp;
            Map<String,String> header = new HashMap<>();
            header.put("Authorization",diskService.getQiNiuToken());
            String str = HttpUtil.doGet(url,null,header);
            if(StringUtils.isEmpty(str)){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"从七牛云获取存储累计使用容量,异常");
            }
            JSONObject json = JSON.parseObject(str);
            if(!"success".equals(json.getString("status"))){
                throw MyException.fail(MinerError.MYB_222222.getCode(),"从七牛云获取存储累计使用容量,异常");
            }
            JSONArray result = json.getJSONObject("data").getJSONArray("result");
            JSONArray value = result.getJSONObject(0).getJSONArray("value");
            timestamp = value.getLongValue(0);
            size = value.getBigDecimal(1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("从七牛云获取存储累计使用容量,异常",e);
        }
        LocalDate localDate = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        log.info("时间:{},存储累计使用量:{}",localDate,size);
        QiniuOneDayAgg qiniuOneDayAgg = new QiniuOneDayAgg().setCreateDate(localDate).setStoreSize(size);
        qiniuOneDayAggService.save(qiniuOneDayAgg);

        //更新平均每日存储使用量
        updateUsedSizeAvg(qiniuOneDayAgg.getId());
        log.info("======================QiniuOneDayAggTask-end===================");
    }

    /**
     *插入数据后，查询过去5天的累计储存量，计算出过去5天的平均储存量，更新到数据中
     * @param id
     */
    public void updateUsedSizeAvg(Long id){
        LambdaQueryWrapper<QiniuOneDayAgg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(QiniuOneDayAgg::getCreateDate,LocalDate.now().minusDays(5));
        queryWrapper.ne(QiniuOneDayAgg::getStoreSize,0);
        queryWrapper.orderByAsc(QiniuOneDayAgg::getCreateDate);
        List<QiniuOneDayAgg> list = qiniuOneDayAggService.list(queryWrapper);
        log.info("查询最近5天的历史数据:{}",JSON.toJSONString(list));
        int length = list.size();
        QiniuOneDayAgg first = list.get(0);
        QiniuOneDayAgg last = list.get(length - 1);
        BigDecimal usedSizeAvg = last.getStoreSize().subtract(first.getStoreSize()).divide(new BigDecimal("5"));
        QiniuOneDayAgg qiniuOneDayAgg = new QiniuOneDayAgg().setId(id).setUsedSizeAvg(usedSizeAvg);
        qiniuOneDayAggService.updateById(qiniuOneDayAgg);
        log.info("更新过去5天平均每天的存储使用量:{}",JSON.toJSONString(qiniuOneDayAgg));
    }


}
