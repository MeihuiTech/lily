package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        try {
            long timestamp = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).toEpochSecond(ZoneOffset.of("+8"));
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
            long seconds = value.getLongValue(0);
            BigDecimal size = value.getBigDecimal(1);
            LocalDate localDate = Instant.ofEpochSecond(seconds).atZone(ZoneOffset.ofHours(8)).toLocalDate();

            QiniuOneDayAgg qiniuOneDayAgg = new QiniuOneDayAgg().setCreateDate(localDate).setStoreSize(size);
            qiniuOneDayAggService.save(qiniuOneDayAgg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("从七牛云获取存储累计使用容量,异常",e);
        }
        log.info("======================QiniuOneDayAggTask-end===================");
    }


}
