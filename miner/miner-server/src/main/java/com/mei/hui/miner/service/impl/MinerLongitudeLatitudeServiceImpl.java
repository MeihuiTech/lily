package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.MinerLongitudeLatitude;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.MinerIpLongitudeLatitudeBO;
import com.mei.hui.miner.feign.vo.MinerLongitudeLatitudeVO;
import com.mei.hui.miner.mapper.MinerLongitudeLatitudeMapper;
import com.mei.hui.miner.service.IMinerLongitudeLatitudeService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/7/16 17:52
 **/
@Service
@Slf4j
public class MinerLongitudeLatitudeServiceImpl extends ServiceImpl<MinerLongitudeLatitudeMapper, MinerLongitudeLatitude> implements IMinerLongitudeLatitudeService {

    @Autowired
    private MinerLongitudeLatitudeMapper minerLongitudeLatitudeMapper;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Value("${gaode.key}")
    private String key;

    /*上报矿工ip*/
    @Override
    public Integer reportMinerIpLongitudeLatitude(MinerIpLongitudeLatitudeBO minerIpLongitudeLatitudeBO) {
        String minerId = minerIpLongitudeLatitudeBO.getMinerId();
        MinerLongitudeLatitude minerLongitudeLatitude = new MinerLongitudeLatitude();
        minerLongitudeLatitude.setMinerId(minerIpLongitudeLatitudeBO.getMinerId());
        minerLongitudeLatitude.setCreateTime(LocalDateTime.now());
        String ip = minerIpLongitudeLatitudeBO.getIp();
        String trueIp = "";
        // 判断ip里的内容是ip还是域名
        if (CommonUtil.isIp(ip)){
            log.info("ip为：【{}】",ip);
            trueIp = ip;
            minerLongitudeLatitude.setIp(ip);
        } else {
            log.info("域名为：【{}】",ip);
            try {
                trueIp = InetAddress.getByName(ip).getHostAddress();
                minerLongitudeLatitude.setIp(trueIp);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                throw MyException.fail(MinerError.MYB_222222.getCode(),"域名转ip错误");
            }
        }

        // 高德api
        /*// 通过ip查询经纬度
        String apiStr = selectGaoDeLongitudeLatitudeByIp(trueIp);
        log.info("通过ip查询经纬度出参：【{}】",apiStr);
        if (StringUtils.isEmpty(apiStr)){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"通过ip查询不到经纬度");
        }
        JSONObject apiJson = JSON.parseObject(apiStr);
        String location = apiJson.getString("location");
        if (StringUtils.isNotEmpty(location) && location.split(",").length > 0){
            minerLongitudeLatitude.setLongitude(new BigDecimal(location.split(",")[0]));
            minerLongitudeLatitude.setLatitude(new BigDecimal(location.split(",")[1]));
        }
        String country = apiJson.getString("country") == null?"":apiJson.getString("country");
        String province = apiJson.getString("province") == null?"":apiJson.getString("province");
        String city = apiJson.getString("city") == null?"":apiJson.getString("city");
        minerLongitudeLatitude.setAddress(country + "," + province + "," + city);
*/

        // ip-api通过ip查询经纬度
        String apiStr = selectIpApiLongitudeLatitudeByIp(trueIp);
        log.info("通过ip查询经纬度出参：【{}】",apiStr);
        if (StringUtils.isNotEmpty(apiStr)){
            JSONObject json = JSON.parseObject(apiStr);
            String country = json.getString("country");
            String regionName = json.getString("regionName");
            String city = json.getString("city");
            String lat = json.getString("lat");
            String lon = json.getString("lon");
            minerLongitudeLatitude.setLongitude(new BigDecimal(lon));
            minerLongitudeLatitude.setLatitude(new BigDecimal(lat));
            minerLongitudeLatitude.setAddress(country+","+regionName+","+city);
        }

        // 查询哪些是自己的矿工
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.list();
        log.info("查询矿工表里所有的矿工出参：【{}】",JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList != null && sysMinerInfoList.size() > 0){
            List<String> myMinerIdList = sysMinerInfoList.stream().map(v -> {
                return v.getMinerId();
            }).collect(Collectors.toList());
            log.info("所有的矿工list：【{}】",JSON.toJSON(myMinerIdList));
            if (myMinerIdList.contains(minerId)){
                minerLongitudeLatitude.setType(1);
            } else {
                minerLongitudeLatitude.setType(0);
            }
        }

        // 查询矿工节点经纬度表里该minerId是否已经存在，如果不存在则插入，存在则更新
        QueryWrapper<MinerLongitudeLatitude> queryWrapper = new QueryWrapper<>();
        MinerLongitudeLatitude selectMinerLongitudeLatitude = new MinerLongitudeLatitude();
        selectMinerLongitudeLatitude.setMinerId(minerId);
        queryWrapper.setEntity(selectMinerLongitudeLatitude);
        MinerLongitudeLatitude dbMinerLongitudeLatitude = minerLongitudeLatitudeMapper.selectOne(queryWrapper);
        log.info("查询矿工节点经纬度表里该minerId：【{}】是否已经存在出参：【{}】",minerId,JSON.toJSON(dbMinerLongitudeLatitude));

        Integer count = 0;
        if (dbMinerLongitudeLatitude == null){
            count = minerLongitudeLatitudeMapper.insert(minerLongitudeLatitude);
        } else {
            minerLongitudeLatitude.setId(dbMinerLongitudeLatitude.getId());
            minerLongitudeLatitude.setUpdateTime(LocalDateTime.now());
            count = minerLongitudeLatitudeMapper.updateById(minerLongitudeLatitude);
        }

        return count;
    }

    /**
     * 通过ip查询经纬度-高德
     * @param ip
     * @return
     */
    public String selectGaoDeLongitudeLatitudeByIp(String ip) {
        log.info("通过ip查询经纬度-高德入参:【{}】",ip);
        try {
            String url = "https://restapi.amap.com/v5/ip?type=4&ip=" + ip + "&key="+key;
            String rt = HttpUtil.doGet(url,"");
            if(StringUtils.isEmpty(rt)){
                log.error("通过ip查询经纬度-高德失败");
                return null;
            }
            return rt;
        } catch (Exception e) {
            log.error("查询通过ip查询经纬度-高德接口异常:",e);
            return null;
        }
    }

    /**
     * 通过ip查询经纬度-ip-api
     * @param ip
     * @return
     */
    public String selectIpApiLongitudeLatitudeByIp(String ip) {
        log.info("通过ip查询经纬度-ip-api入参:【{}】",ip);
        try {
            String url = "http://ip-api.com/json/" + ip + "?lang=zh-CN";
            String rt = HttpUtil.doGet(url,"");
            if(StringUtils.isEmpty(rt)){
                log.error("通过ip：【{}】查询经纬度-ip-api失败",ip);
                return null;
            }
            return rt;
        } catch (Exception e) {
            log.error("查询通过ip：【{}】查询经纬度-ip-api接口异常:",ip,e);
            return null;
        }
    }

    /*查询fil矿工id节点地图*/
    @Override
    public List<MinerLongitudeLatitudeVO> selectMap() {
        List<MinerLongitudeLatitudeVO> minerLongitudeLatitudeVOList = minerLongitudeLatitudeMapper.selectMinerLongitudeLatitudeVOList();
        minerLongitudeLatitudeVOList.stream().forEach(v->{
            String[] ipArr = v.getIp().split("\\.");
            v.setIp(ipArr[0] + ".**.**." + ipArr[3]);
        });
        return minerLongitudeLatitudeVOList;
    }

    /**
     * 更新矿工所在地理位置
     * @return
     */
    public Result initMinerIp(){
        int pageNum = 1;
        int pageSize = 20;
        while (true) {
            LambdaQueryWrapper<MinerLongitudeLatitude> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.isNull(MinerLongitudeLatitude::getAddress);
            IPage<MinerLongitudeLatitude> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);

            List<MinerLongitudeLatitude> batch = new ArrayList<>();
            for(MinerLongitudeLatitude v : page.getRecords()){
                MinerLongitudeLatitude vo = new MinerLongitudeLatitude();
                String result = null;
                try {
                    result = HttpUtil.doGet("http://ip-api.com/json/" + v.getIp() + "?lang=zh-CN", "");
                } catch (Exception e) {
                }
                if(StringUtils.isEmpty(result)){
                    continue;
                }
                JSONObject json = JSON.parseObject(result);
                String country = json.getString("country");
                String regionName = json.getString("regionName");
                String city = json.getString("city");
                String lat = json.getString("lat");
                String lon = json.getString("lon");
                vo.setAddress(country+","+regionName+","+city);
                vo.setId(v.getId());
                vo.setLatitude(new BigDecimal(lat));
                vo.setLongitude(new BigDecimal(lon));
                batch.add(vo);
            }
            if (page.getRecords().size() == 0) {
                break;
            }
            this.updateBatchById(batch);
            if (page.getRecords().size() < pageSize) {
                break;
            } else {
                pageNum ++;
            }
        }
        return Result.OK;
    }
}
