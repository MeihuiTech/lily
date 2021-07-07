package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.common.Constants;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.DiskBO;
import com.mei.hui.miner.feign.vo.DiskVO;
import com.mei.hui.miner.service.DiskService;
import com.mei.hui.miner.service.ISysMinerInfoService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DiskServiceImpl implements DiskService {

    @Autowired
    private RuoYiConfig ruoYiConfig;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysMinerInfoService minerInfoService;

    public Result<DiskVO> diskSizeInfo(DiskBO diskBO){
        String totalDiskSizeUrl = "sum by(cluster)(kodo_qbs_blkmaster_physical_space_capacity_bytes{service=\"blkmaster\"})";
        BigDecimal totalDiskSize = getDiskSize(totalDiskSizeUrl);
        log.info("获取磁盘总容量:{}",totalDiskSize);

        String availDiskSizeUrl = "sum by(cluster)(kodo_qbs_blkmaster_physical_space_avail_bytes{service=\"blkmaster\"})";
        BigDecimal availDiskSize = getDiskSize(availDiskSizeUrl);
        log.info("获取磁盘剩余可用容量:{}",availDiskSize);

        BigDecimal logicalAvailSize = miscconfigs();
        log.info("剩余可写逻辑容量估算值:{}",logicalAvailSize);

        BigDecimal minerUsedDiskSize = getMinerUsedDiskSize(diskBO.getMinerId());
        log.info("旷工已用存储量:{}",minerUsedDiskSize);


        DiskVO diskVO = new DiskVO()
                .setAvailDiskSize(availDiskSize)
                .setUsedDiskSize(totalDiskSize.subtract(availDiskSize))
                .setLogicalAvailSize(logicalAvailSize)
                .setMinerUsedDiskSize(minerUsedDiskSize);
        return Result.success(diskVO);
    }

    /**
     * 获取旷工已用存储量
     * @param minerId
     * @return
     */
    public BigDecimal getMinerUsedDiskSize(String minerId){
        Map<String, BigDecimal> map = allbucketInfo();

        LambdaQueryWrapper<SysMinerInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMinerInfo::getMinerId,minerId);
        List<SysMinerInfo> list = minerInfoService.list(queryWrapper);
        log.info("获取旷工信息:{}",JSON.toJSONString(list));
        if(list.size() == 0){
            return new BigDecimal("0");
        }
        return map.get(list.get(0).getBucket());
    }
    /**
     * 获取总容量
     * @return
     * @throws UnsupportedEncodingException
     */
    public BigDecimal getDiskSize(String metric) {
        try {
            String url = ruoYiConfig.getQiNiuPrometheusUrl()+"/api/v1/query?query="+ URLEncoder.encode(metric,"UTF-8");
            Map<String,String> header = new HashMap<>();
            header.put("Authorization",getQiNiuToken());
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
            return value.getBigDecimal(1);
        } catch (Exception e) {
            log.error("调用七牛接口异常:",e);
            return new BigDecimal("0");
        }
    }
    /**
     * 获取七牛云token 用于接口调用
     * @return
     */
    public String getQiNiuToken(){
        String qi_niu_token = redisUtil.get(Constants.qi_niu_token);
        if(StringUtils.isNotEmpty(qi_niu_token)){
            return qi_niu_token;
        }
        String domain = ruoYiConfig.getQiNiuEcloudUrl()+"/api/proxy/admin-acc/login/signin";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email",ruoYiConfig.getQiNiuEmail());
        jsonObject.put("password",ruoYiConfig.getQiNiuPassword());
        String result = HttpUtil.doPost(domain,jsonObject.toJSONString());
        if(StringUtils.isEmpty(result)){
            throw MyException.fail(ErrorCode.MYB_111111.getCode(),"获取七牛token失败");
        }
        JSONObject json = JSON.parseObject(result);
        String token = json.getString("token");
        redisUtil.set(Constants.qi_niu_token,token,6, TimeUnit.DAYS);
        return token;
    }
    /**
     * 查询集群剩余科协逻辑容量估算值
     */
    public BigDecimal miscconfigs() {
        Map<String,String> header = new HashMap<>();
        header.put("Authorization",getQiNiuToken());
        String rt = HttpUtil.doGet(ruoYiConfig.getQiNiuEcloudUrl()+"/api/proxy/blkmaster/z0/miscconfigs", null, header);
        String str = JSON.parseObject(rt).getString("default_write_mode");
        if(StringUtils.isEmpty(str)){
            log.error("调用七牛接口失败");
            return new BigDecimal("0");
        }
        String EC_N = str.substring(str.lastIndexOf("N")+1, str.lastIndexOf("M"));
        String EC_M = str.substring(str.lastIndexOf("M")+1);
        log.info("EC_N={},EC_M={}",EC_N,EC_M);
        String result = HttpUtil.doGet(ruoYiConfig.getQiNiuEcloudUrl() + "/api/proxy/blkmaster/z0/tool/scale/n/" + EC_N + "/m/" + EC_M + "/expected/0/disk_size/1000000000/disk_per_host/16", null, header);
        if(StringUtils.isEmpty(result)){
            log.error("调用七牛接口失败");
            return new BigDecimal("0");
        }
        BigDecimal logical_avail_size = JSON.parseObject(result).getBigDecimal("logical_avail_size");
        return logical_avail_size;
    }

    /**
     * 获取每个旷工已使用存储量
     * @return
     */
    public Map<String,BigDecimal> allbucketInfo() {
        int count = minerInfoService.count();
        log.info("平台旷工数量:{}",count);

        Map<String,BigDecimal> map = new HashMap<>();
        Map<String,String> header = new HashMap<>();
        header.put("Authorization",getQiNiuToken());
        String result = HttpUtil.doGet(ruoYiConfig.getQiNiuEcloudUrl()+"/api/proxy/uc/qbox/admin/allbuckets?limit="+count, null, header);
        if(StringUtils.isEmpty(result)){
            log.error("调用七牛接口失败");
            return map;
        }
        JSONArray buckets = JSON.parseObject(result).getJSONArray("buckets");
        for(int i=0;i<buckets.size();i++){
            JSONObject jsonObject = buckets.getJSONObject(i);
            String bucketName = jsonObject.getString("id");
            BigDecimal storageSize = jsonObject.getBigDecimal("storage_size");
            map.put(bucketName,storageSize);
        }
        return map;
    }

}
