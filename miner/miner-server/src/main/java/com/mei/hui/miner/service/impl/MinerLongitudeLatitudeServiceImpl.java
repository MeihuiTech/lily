package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.entity.MinerLongitudeLatitude;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.MinerIpLongitudeLatitudeBO;
import com.mei.hui.miner.feign.vo.MinerLongitudeLatitudeVO;
import com.mei.hui.miner.mapper.MinerLongitudeLatitudeMapper;
import com.mei.hui.miner.service.IMinerLongitudeLatitudeService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
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
        String ip = minerIpLongitudeLatitudeBO.getIp();
        // 判断ip里的内容是ip还是域名
        if (CommonUtil.isIp(ip)){
            log.info("ip为：【{}】",ip);
            minerLongitudeLatitude.setIp(ip);
        } else {
            log.info("域名为：【{}】",ip);
            try {
                minerLongitudeLatitude.setIp(InetAddress.getByName(ip).getHostAddress());
            } catch (UnknownHostException e) {
                log.info("域名转ip错误，ip：【{}】",ip);
                e.printStackTrace();
            }
        }

        // 通过ip查询经纬度
        String aipStr = selectLongitudeLatitudeByIp(ip);
        JSONObject apiJson = JSON.parseObject(aipStr);
        String location = apiJson.getString("location");
        if (StringUtils.isNotEmpty(location)){
            minerLongitudeLatitude.setLongitude(new BigDecimal(location.split(",")[0]));
            minerLongitudeLatitude.setLatitude(new BigDecimal(location.split(",")[1]));
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

        String country = apiJson.getString("country");
        String province = apiJson.getString("province");
        String city = apiJson.getString("city");
        minerLongitudeLatitude.setAddress(country + province + city);
        minerLongitudeLatitude.setCreateTime(LocalDateTime.now());

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
     * 通过ip查询经纬度
     * @param ip
     * @return
     */
    public String selectLongitudeLatitudeByIp(String ip) {
        log.info("通过ip查询经纬度入参:【{}】",ip);
        try {
            String url = "https://restapi.amap.com/v5/ip?type=4&ip=" + ip + "&key="+key;
            String rt = HttpUtil.doGet(url,"");
            if(StringUtils.isEmpty(rt)){
                log.error("通过ip查询经纬度失败");
                return null;
            }
            return rt;
        } catch (Exception e) {
            log.error("查询通过ip查询经纬度接口异常:",e);
            return null;
        }
    }

    /*查询fil矿工id节点地图*/
    @Override
    public List<MinerLongitudeLatitudeVO> selectMap() {
        List<MinerLongitudeLatitudeVO> minerLongitudeLatitudeVOList = minerLongitudeLatitudeMapper.selectMinerLongitudeLatitudeVOList();
        return minerLongitudeLatitudeVOList;
    }
}
