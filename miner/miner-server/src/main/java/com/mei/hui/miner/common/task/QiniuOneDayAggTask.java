//package com.mei.hui.miner.common.task;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.github.pagehelper.PageHelper;
//import com.mei.hui.config.HttpUtil;
//import com.mei.hui.config.jwtConfig.RuoYiConfig;
//import com.mei.hui.miner.common.MinerError;
//import com.mei.hui.miner.entity.*;
//import com.mei.hui.miner.service.*;
//import com.mei.hui.util.CurrencyEnum;
//import com.mei.hui.util.DateUtils;
//import com.mei.hui.util.MyException;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.UnsupportedEncodingException;
//import java.math.BigDecimal;
//import java.net.URLEncoder;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * chia币定时任务
// */
//@Configuration
//@EnableScheduling
//@Slf4j
//public class QiniuOneDayAggTask {
//
//    @Autowired
//    private QiniuOneDayAggService qiniuOneDayAggService;
//    @Autowired
//    private QiniuStoreConfigService qiniuStoreConfigService;
//    @Autowired
//    private DiskService diskService;
//
//    /**
//     * 先从 qiniu_store_config 中查询集群数量，然后再查询每个集群的 请求地址和账号密码
//     * 每天凌晨1点获取七牛云累计存储使用量
//     */
//    @Scheduled(cron = "0 5 0 * * ?")
//    public void run() {
//        log.info("======================QiniuOneDayAggTask-start===================");
//        //先从 qiniu_store_config 中查询集群数量
//        QueryWrapper<QiniuStoreConfig> queryWrapper = new QueryWrapper();
//        queryWrapper.select("distinct cluster_name");
//        List<QiniuStoreConfig> configs = qiniuStoreConfigService.list(queryWrapper);
//        log.info("查询所有集群:{}",JSON.toJSONString(configs));
//        if(configs.size() ==0){
//            return;
//        }
//        for(QiniuStoreConfig config : configs){
//            LambdaQueryWrapper<QiniuStoreConfig> lambdaQueryWrapper = new LambdaQueryWrapper();
//            lambdaQueryWrapper.eq(QiniuStoreConfig::getClusterName,config.getClusterName());
//            List<QiniuStoreConfig> list = qiniuStoreConfigService.list(lambdaQueryWrapper);
//            log.info("获取集群{}的配置信息:{}",config.getClusterName(),JSON.toJSONString(list));
//            QiniuStoreConfig storeConfig = list.get(0);
//
//            //查询集群存储累计使用量
//            QiniuOneDayAgg qiniuOneDayAgg = getCluserOneDayAgg(storeConfig);
//            //更新平均每日存储使用量
//            updateUsedSizeAvg(qiniuOneDayAgg);
//        }
//        log.info("======================QiniuOneDayAggTask-end===================");
//    }
//
//
//    /**
//     * 获取单个集群累计使用容量
//     * 返回聚合表的主键
//     * @param qiniuStoreConfig
//     * @return
//     */
//    public QiniuOneDayAgg getCluserOneDayAgg(QiniuStoreConfig qiniuStoreConfig){
//        BigDecimal size = new BigDecimal("0");
//        long timestamp = LocalDate.now().minusDays(1).atStartOfDay(ZoneOffset.ofHours(8)).toInstant().getEpochSecond();
//        try {
//            String metric = "avg_over_time(kodo_qbs_blkmaster_physical_space_capacity_bytes[1d]) - avg_over_time(kodo_qbs_blkmaster_physical_space_avail_bytes[1d])";
//            String url = qiniuStoreConfig.getPrometheusDomain()+"/api/v1/query?query="+ URLEncoder.encode(metric,"UTF-8")+"&time="+timestamp;
//            Map<String,String> header = new HashMap<>();
//            header.put("Authorization",diskService.getQiNiuToken(qiniuStoreConfig));
//            String str = HttpUtil.doGet(url,null,header);
//            if(StringUtils.isEmpty(str)){
//                throw MyException.fail(MinerError.MYB_222222.getCode(),"从七牛云获取存储累计使用容量,异常");
//            }
//            JSONObject json = JSON.parseObject(str);
//            if(!"success".equals(json.getString("status"))){
//                throw MyException.fail(MinerError.MYB_222222.getCode(),"从七牛云获取存储累计使用容量,异常");
//            }
//            JSONArray result = json.getJSONObject("data").getJSONArray("result");
//            JSONArray value = result.getJSONObject(0).getJSONArray("value");
//            timestamp = value.getLongValue(0);
//            size = value.getBigDecimal(1);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            log.error("从七牛云获取存储累计使用容量,异常",e);
//        }
//        LocalDate localDate = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
//        log.info("时间:{},存储累计使用量:{}",localDate,size);
//        QiniuOneDayAgg qiniuOneDayAgg = new QiniuOneDayAgg()
//                .setCreateDate(localDate)
//                .setStoreSize(size)
//                .setClusterName(qiniuStoreConfig.getClusterName());
//        qiniuOneDayAggService.save(qiniuOneDayAgg);
//        return qiniuOneDayAgg;
//    }
//    /**
//     *插入数据后，查询过去5天的累计储存量，计算出过去5天的平均储存量，更新到数据中
//     * @param oneDayAgg
//     */
//    public void updateUsedSizeAvg(QiniuOneDayAgg oneDayAgg){
//        LambdaQueryWrapper<QiniuOneDayAgg> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.ge(QiniuOneDayAgg::getCreateDate,LocalDate.now().minusDays(5));
//        queryWrapper.ne(QiniuOneDayAgg::getStoreSize,0);
//        queryWrapper.eq(QiniuOneDayAgg::getClusterName,oneDayAgg.getClusterName());
//        queryWrapper.orderByAsc(QiniuOneDayAgg::getCreateDate);
//        List<QiniuOneDayAgg> list = qiniuOneDayAggService.list(queryWrapper);
//        log.info("查询最近5天的历史数据:{}",JSON.toJSONString(list));
//        BigDecimal usedSizeAvg = new BigDecimal("0");
//        if(list.size() != 0){
//            int length = list.size();
//            QiniuOneDayAgg first = list.get(0);
//            QiniuOneDayAgg last = list.get(length - 1);
//            usedSizeAvg = last.getStoreSize().subtract(first.getStoreSize()).divide(new BigDecimal("5"));
//        }
//        QiniuOneDayAgg qiniuOneDayAgg = new QiniuOneDayAgg().setId(oneDayAgg.getId()).setUsedSizeAvg(usedSizeAvg);
//        qiniuOneDayAggService.updateById(qiniuOneDayAgg);
//        log.info("更新过去5天平均每天的存储使用量:{}",JSON.toJSONString(qiniuOneDayAgg));
//    }
//
// /*   CREATE TABLE `qiniu_one_day_agg` (
//            `id` bigint(20) NOT NULL AUTO_INCREMENT,
//  `store_size` decimal(65,0) NOT NULL COMMENT '累计使用容量,单位Byte',
//            `used_size_avg` decimal(65,0) DEFAULT NULL COMMENT '过去5天平均使用容量',
//            `cluster_name` varchar(32) NOT NULL COMMENT '集群名称',
//            `create_date` date NOT NULL COMMENT '创建日期',
//    PRIMARY KEY (`id`),
//    UNIQUE KEY `clusterName_createDate_index` (`cluster_name`,`create_date`) USING BTREE
//) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COMMENT='七牛存储使用容量聚合表';*/
//
//
//
//
//}
