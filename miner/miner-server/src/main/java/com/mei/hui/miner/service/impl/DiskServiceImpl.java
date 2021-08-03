package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.QiniuOneDayAgg;
import com.mei.hui.miner.entity.QiniuStoreConfig;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.*;
import com.mei.hui.miner.service.DiskService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.miner.service.QiniuOneDayAggService;
import com.mei.hui.miner.service.QiniuStoreConfigService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiskServiceImpl implements DiskService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysMinerInfoService minerInfoService;
    @Autowired
    private QiniuOneDayAggService qiniuOneDayAggService;
    @Autowired
    private QiniuStoreConfigService qiniuStoreConfigService;
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    /*获取七牛云集群硬盘容量和宽带信息*/
    @Override
    public List<QiniuVO> selectDiskSizeAndBroadbandList(List<SysMinerInfo> sysMinerInfoList) {
        List<QiniuStoreConfig> qiniuStoreConfigList = new ArrayList<>();
        List<String> idcnameList = new ArrayList<>();
        for (SysMinerInfo sysMinerInfo:sysMinerInfoList){
            if (Constants.STORETYPEQINIU.equals(sysMinerInfo.getStoreType())){
                //获取当前选择矿工的七牛配置信息
                String minerId = sysMinerInfo.getMinerId();
                QiniuStoreConfig storeConfig = selectQiniuStoreConfigByMinerId(minerId);
                if(storeConfig == null){
                    throw MyException.fail(MinerError.MYB_222222.getCode(),"矿工七牛存储配置信息为空");
                }
                String idcname = storeConfig.getIdcname();
                if (!idcnameList.contains(idcname)){
                    idcnameList.add(idcname);
                    qiniuStoreConfigList.add(storeConfig);
                }
            }
        }
        log.info("idcnameList：【{}】，qiniuStoreConfigList:【{}】",idcnameList,JSON.toJSON(qiniuStoreConfigList));

        // 循环集群
        List<QiniuVO> qiniuVOList = new ArrayList<>();
        for (QiniuStoreConfig dbQiniuStoreConfig:qiniuStoreConfigList) {
            QiniuVO qiniuVO = new QiniuVO();
            // 容量
            DiskSizeVO diskSizeVO = diskSizeInfo(dbQiniuStoreConfig);
            log.info("获取七牛云集群硬盘容量出参：【{}】",JSON.toJSON(diskSizeVO));
            qiniuVO.setDiskSizeVO(diskSizeVO);

            // 宽带
            BroadbandVO broadbandVO = broadband(dbQiniuStoreConfig);
            log.info("获取宽带信息出参：【{}】",JSON.toJSON(broadbandVO));
            qiniuVO.setBroadbandVO(broadbandVO);
            qiniuVOList.add(qiniuVO);
        }

        return qiniuVOList;
    }

    /**
     * 获取七牛云集群硬盘容量
     * @return
     */
    public DiskSizeVO diskSizeInfo(QiniuStoreConfig storeConfig){
        try {
            String totalDiskSizeUrl = "sum by(cluster)(kodo_qbs_blkmaster_physical_space_capacity_bytes{service=\"blkmaster\"})";
            log.info("获取磁盘总容量入参storeConfig：【{}】,totalDiskSizeUrl：【{}】",JSON.toJSON(storeConfig),totalDiskSizeUrl);
            BigDecimal totalDiskSize = getDiskSize(storeConfig,totalDiskSizeUrl);
            log.info("获取磁盘总容量出参:{}",totalDiskSize);

            String availDiskSizeUrl = "sum by(cluster)(kodo_qbs_blkmaster_physical_space_avail_bytes{service=\"blkmaster\"})";
            log.info("获取磁盘总容量入参storeConfig：【{}】,availDiskSizeUrl：【{}】",JSON.toJSON(storeConfig),availDiskSizeUrl);
            BigDecimal availDiskSize = getDiskSize(storeConfig,availDiskSizeUrl);
            log.info("获取磁盘剩余可用容量出参:{}",availDiskSize);

            List<QiniuStoreConfig> configs = selectQiniuStoreConfigListByUserId(storeConfig.getIdcname());
            List<MinerDiskSizeVO> minerUsedDiskSizeVOList = new ArrayList<>();
            // 循环获得每个矿工id
            for (QiniuStoreConfig config:configs){
                log.info("dbQiniuStoreConfig：【{}】",JSON.toJSON(config));
                String dbMinerId = config.getMinerId();
                BigDecimal minerUsedDiskSize = getMinerUsedDiskSize(config);
                log.info("获取矿工为：【{}】  已用存储量:【{}】",dbMinerId,minerUsedDiskSize);
                MinerDiskSizeVO minerDiskSizeVO = new MinerDiskSizeVO();
                minerDiskSizeVO.setMinerId(dbMinerId);
                minerDiskSizeVO.setDiskSize(minerUsedDiskSize);
                log.info("minerDiskSizeVO：【{}】",JSON.toJSON(minerDiskSizeVO));
                minerUsedDiskSizeVOList.add(minerDiskSizeVO);
            }

            DiskSizeVO diskSizeVO = new DiskSizeVO();
            diskSizeVO.setAllDiskSize(totalDiskSize);
            diskSizeVO.setAvailDiskSize(availDiskSize);
            diskSizeVO.setUsedDiskSize(totalDiskSize.subtract(availDiskSize));
            diskSizeVO.setMinerUsedDiskSizeVOList(minerUsedDiskSizeVOList);
            return diskSizeVO;
        } catch (Exception e) {
            log.error("获取宽带信息报错",e);
            return null;
        }
    }

    /**
     * 根据minerId查询矿工存储服务配置表
     * @param minerId
     * @return
     */
    public QiniuStoreConfig selectQiniuStoreConfigByMinerId(String minerId){
        log.info("根据minerId查询矿工存储服务配置表入参minerId:【{}】",minerId);
        LambdaQueryWrapper<QiniuStoreConfig> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(QiniuStoreConfig::getMinerId,minerId);
        QiniuStoreConfig storeConfig = qiniuStoreConfigService.getOne(queryWrapper);
        log.info("根据minerId查询矿工存储服务配置表出参:{}",JSON.toJSONString(storeConfig));
        return storeConfig;
    }

    /**
     * 根据idcname查询矿工存储服务配置表
     * @return
     */
    public List<QiniuStoreConfig> selectQiniuStoreConfigListByUserId(String idcname){
        SysMinerInfo sysMinerInfo = new SysMinerInfo();
        sysMinerInfo.setUserId( HttpRequestUtil.getUserId());
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoListBySysMinerInfo(sysMinerInfo);
        List<String> minerIds = sysMinerInfoList.stream().map(v -> v.getMinerId()).collect(Collectors.toList());
        if(minerIds.size() == 0){
            return null;
        }
        LambdaQueryWrapper<QiniuStoreConfig> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(QiniuStoreConfig::getMinerId,minerIds);
        queryWrapper.eq(QiniuStoreConfig::getIdcname,idcname);
        List<QiniuStoreConfig> list = qiniuStoreConfigService.list(queryWrapper);
        log.info("根据minerId查询矿工存储服务配置表出参:{}",JSON.toJSONString(list));
        return list;
    }

    /**
     * 获取矿工已用存储量
     * @return
     */
    public BigDecimal getMinerUsedDiskSize(QiniuStoreConfig storeConfig){
        Map<String, BigDecimal> map = allbucketInfo(storeConfig);
        log.info("获取每个矿工已使用存储量出参：【{}】",map.toString());
        return map.get(storeConfig.getBucket());
    }
    /**
     * 获取总容量
     * @return
     * @throws UnsupportedEncodingException
     */
    public BigDecimal getDiskSize(QiniuStoreConfig qiniuStoreConfig,String metric) {
        try {
            String url = qiniuStoreConfig.getPrometheusDomain()+"/api/v1/query?query="+ URLEncoder.encode(metric,"UTF-8");
            Map<String,String> header = new HashMap<>();
            header.put("Authorization",getQiNiuToken(qiniuStoreConfig));
            String rt = HttpUtil.doPost(url,"",header);
            if(StringUtils.isEmpty(rt)){
                log.error("获取集群总物理容量失败");
                return new BigDecimal("0");
            }
            JSONObject json = JSON.parseObject(rt);
            String status = json.getString("status");
            if(!"success".equals(status)){
                log.error("获取集群总物理容量失败");
                return new BigDecimal("0");
            }
            JSONObject data = json.getJSONObject("data");
            JSONArray result = data.getJSONArray("result");
            JSONArray value = result.getJSONObject(0).getJSONArray("value");
            log.info("value:【{}】",value);
            return value.getBigDecimal(1);
        } catch (Exception e) {
            log.error("调用七牛接口异常:",e);
            return null;
        }
    }
    /**
     * 获取七牛云token 用于接口调用
     * @return
     */
    public String getQiNiuToken(QiniuStoreConfig qiniuStoreConfig){
        log.info("获取七牛云token 用于接口调用入参：【{}】",JSON.toJSON(qiniuStoreConfig));
        String qiniuUserName = AESUtil.decrypt(qiniuStoreConfig.getUserName());
        String qiniuPassWord = AESUtil.decrypt(qiniuStoreConfig.getPassWord());
        String redisKey = String.format(Constants.qi_niu_token,qiniuStoreConfig.getClusterName());
        String qi_niu_token = redisUtil.get(redisKey);
        if(StringUtils.isNotEmpty(qi_niu_token)){
            return qi_niu_token;
        }
        String domain = qiniuStoreConfig.getEcloudDomain()+"/api/proxy/admin-acc/login/signin";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email",qiniuUserName);
        jsonObject.put("password",qiniuPassWord);
        String result = HttpUtil.doPost(domain,jsonObject.toJSONString());
        if(StringUtils.isEmpty(result)){
            throw MyException.fail(ErrorCode.MYB_111111.getCode(),"获取七牛token失败");
        }
        JSONObject json = JSON.parseObject(result);
        String token = json.getString("token");
        redisUtil.set(redisKey,token,5, TimeUnit.DAYS);
        return token;
    }
    /**
     * 查询集群剩余可写逻辑容量估算值
     */
    public BigDecimal miscconfigs(QiniuStoreConfig qiniuStoreConfig) {
        try {
            Map<String,String> header = new HashMap<>();
            header.put("Authorization",getQiNiuToken(qiniuStoreConfig));
            String rt = HttpUtil.doGet(qiniuStoreConfig.getEcloudDomain()+"/api/proxy/blkmaster/z0/miscconfigs", null, header);
            String str = JSON.parseObject(rt).getString("default_write_mode");
            String EC_N = str.substring(str.lastIndexOf("N")+1, str.lastIndexOf("M"));
            String EC_M = str.substring(str.lastIndexOf("M")+1);
            log.info("EC_N={},EC_M={}",EC_N,EC_M);
            String result = HttpUtil.doGet(qiniuStoreConfig.getEcloudDomain() + "/api/proxy/blkmaster/z0/tool/scale/n/" + EC_N + "/m/" + EC_M + "/expected/0/disk_size/1000000000/disk_per_host/16", null, header);
            BigDecimal logical_avail_size = JSON.parseObject(result).getBigDecimal("logical_avail_size");
            return logical_avail_size;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("查询集群剩余可写逻辑容量估算值,异常:",e);
            return null;
        }
    }

    /**
     * 获取每个矿工已使用存储量
     * @return
     */
    public Map<String,BigDecimal> allbucketInfo(QiniuStoreConfig qiniuStoreConfig) {
        log.info("获取每个矿工已使用存储量入参：【{}】",JSON.toJSON(qiniuStoreConfig));
        int count = minerInfoService.count();
        log.info("平台矿工数量:{}",count);

        Map<String,BigDecimal> map = new HashMap<>();
        Map<String,String> header = new HashMap<>();
        header.put("Authorization",getQiNiuToken(qiniuStoreConfig));
        String result = HttpUtil.doGet(qiniuStoreConfig.getEcloudDomain()+"/api/proxy/uc/qbox/admin/allbuckets?limit="+count, null, header);
        if(StringUtils.isEmpty(result)){
            log.error("调用七牛接口失败");
            return map;
        }
        JSONArray buckets = JSON.parseObject(result).getJSONArray("buckets");
        log.info("七牛云-buckets：【{}】",buckets.toJSONString());
        for(int i=0;i<buckets.size();i++){
            JSONObject jsonObject = buckets.getJSONObject(i);
            String bucketName = jsonObject.getString("id");
            BigDecimal storageSize = jsonObject.getBigDecimal("storage_size");
            map.put(bucketName,storageSize);
        }
        return map;
    }

    /**
     * 查询集群剩余可写逻辑容量估算值
     * available_capacity=剩余可写物理容量=集群剩余可写逻辑容量*(EC-N+EC-M)/EC-N
     * 预测天数 days= available_capacity / used_avg
     */
    public Integer days(QiniuStoreConfig qiniuStoreConfig,BigDecimal logicalAvailSize,BigDecimal usedSizeAvg) {
        log.info("logicalAvailSize:{},usedSizeAvg:{}",logicalAvailSize,usedSizeAvg);
        try {
            Map<String,String> header = new HashMap<>();
            header.put("Authorization",getQiNiuToken(qiniuStoreConfig));
            String rt = HttpUtil.doGet(qiniuStoreConfig.getEcloudDomain()+"/api/proxy/blkmaster/z0/miscconfigs", null, header);
            String str = JSON.parseObject(rt).getString("default_write_mode");
            String EC_N = str.substring(str.lastIndexOf("N")+1, str.lastIndexOf("M"));
            String EC_M = str.substring(str.lastIndexOf("M")+1);
            log.info("EC_N={},EC_M={}",EC_N,EC_M);
            BigDecimal available_capacity = logicalAvailSize.multiply(new BigDecimal(EC_N).add(new BigDecimal(EC_M))).divide(new BigDecimal(EC_N));
            log.info("available_capacity:{}",available_capacity);
            return available_capacity.divide(usedSizeAvg,2,BigDecimal.ROUND_HALF_UP).intValue();
        } catch (Exception e) {
            log.error("计算剩余使用天数,异常:",e);
            return null;
        }
    }

    /**
     * 过去5天平均使用容量
     * @return
     */
    public BigDecimal usedSizeAvg(){
        try {
            //获取过去5天，平均每天的使用容量
            LambdaQueryWrapper<QiniuOneDayAgg> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(QiniuOneDayAgg::getCreateDate, LocalDate.now().minusDays(1));
            List<QiniuOneDayAgg> list = qiniuOneDayAggService.list(queryWrapper);
            log.info("获取过去5天,平均每天的使用容量:{}",JSON.toJSONString(list));
            return list.get(0).getUsedSizeAvg();
        } catch (Exception e) {
            log.error("过去5天平均使用容量",e);
            return null;
        }
    }


    /**
     * 获取宽带信息
     * @return
     */
    public BroadbandVO broadband(QiniuStoreConfig storeConfig) {
        try {
            // 查24小时的数据
            Long yesterdayTimeLong = LocalDateTime.now().plusHours(-24L).toEpochSecond(ZoneOffset.of("+8"));
            Long nowTimeLong = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));

            // 上传带宽，单位 bps
            String upBroadbandMetric = "sum (rate(service_request_length{idcname=\"" + storeConfig.getIdcname() + "\",api=~\"up.*|s3apiv2.putobject.*|s3apiv2.postobject|s3apiv2.uploadpart.*\"}[1m]))*8";
            List<BroadbandUpDownVO> upBroadbandVOList = getQiniuData(storeConfig,upBroadbandMetric,yesterdayTimeLong,nowTimeLong);
            log.info("获取上传带宽出参:{}",JSON.toJSON(upBroadbandVOList));

            // 下载带宽，单位 bps
            String downBroadbandMetric = "sum (rate(service_response_length{idcname=\"" + storeConfig.getIdcname() + "\",api=~\"io.get|s3apiv2.getobject\"}[1m]))*8";
            List<BroadbandUpDownVO> downBroadbandVOList = getQiniuData(storeConfig,downBroadbandMetric,yesterdayTimeLong,nowTimeLong);
            log.info("获取下载带宽出参:{}",JSON.toJSON(downBroadbandVOList));

            BroadbandVO broadbandVO = new BroadbandVO();
            broadbandVO.setBroadbandUpVOList(upBroadbandVOList);
            broadbandVO.setBroadbandDownVOList(downBroadbandVOList);

            return broadbandVO;
        }catch (Exception e){
            log.error("获取宽带信息报错",e);
            return null;
        }
    }

    /**
     * 查询七牛云一段时间内上传/下载的带宽、IOPS和响应时间95值
     * @return
     * @throws UnsupportedEncodingException
     */
    public List<BroadbandUpDownVO> getQiniuData(QiniuStoreConfig qiniuStoreConfig,String metric,Long startTime, Long endTime) {
        log.info("查询七牛云一段时间内上传/下载的带宽、IOPS和响应时间95值入参:qiniuStoreConfig【{}】,metric：【{}】,startTime:【{}】,endTime：【{}】",JSON.toJSON(qiniuStoreConfig),metric,startTime,endTime);
        try {
            String url = qiniuStoreConfig.getPrometheusDomain()+"/api/v1/query_range?query="+ URLEncoder.encode(metric,"UTF-8") + "&start=" + startTime + "&end=" + endTime + "&step=2m";
            Map<String,String> header = new HashMap<>();
            header.put("Authorization",getQiNiuToken(qiniuStoreConfig));
            String rt = HttpUtil.doPost(url,"",header);
            if(StringUtils.isEmpty(rt)){
                log.error("查询七牛云一段时间内上传/下载的带宽、IOPS和响应时间95值失败");
                return null;
            }
            JSONObject json = JSON.parseObject(rt);
            String status = json.getString("status");
            if(!"success".equals(status)){
                log.error("查询七牛云一段时间内上传/下载的带宽、IOPS和响应时间95值失败");
                return null;
            }
            JSONObject data = json.getJSONObject("data");
            JSONArray result = data.getJSONArray("result");
            JSONArray value = result.getJSONObject(0).getJSONArray("values");
            log.info("value：【{}】",value.toJSONString());
            List<BroadbandUpDownVO> broadbandUpDownVOList = new ArrayList<>();
            if (value.size() > 0){
                for (int i=0;i<value.size();i++){
                    BroadbandUpDownVO broadbandUpDownVO = new BroadbandUpDownVO();
//                    log.info("value.getJSONArray(i)：【{}】",value.getJSONArray(i).toJSONString());
                    broadbandUpDownVO.setTimestamp(Long.valueOf(value.getJSONArray(i).get(0) + ""));
                    broadbandUpDownVO.setValue(new BigDecimal(value.getJSONArray(i).get(1)+""));
                    broadbandUpDownVOList.add(broadbandUpDownVO);
                }
            }
            return broadbandUpDownVOList;
        } catch (Exception e) {
            log.error("查询七牛云一段时间内上传/下载的带宽、IOPS和响应时间95值接口异常:",e);
            return null;
        }
    }


}
