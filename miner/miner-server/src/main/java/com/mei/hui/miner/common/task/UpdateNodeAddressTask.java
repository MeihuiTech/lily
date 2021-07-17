package com.mei.hui.miner.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.entity.MinerLongitudeLatitude;
import com.mei.hui.miner.service.IMinerLongitudeLatitudeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
@Slf4j
public class UpdateNodeAddressTask {

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private IMinerLongitudeLatitudeService minerLongitudeLatitudeService;

    /*每天早晨6点执行*/
    //@Scheduled(cron = "0 0 6 * * ?")
    public void run() {
        log.info("======================UpdateNodeAddressTask-start===================");
       /* if("dev".equals(env)){
            log.info("开发环境,不执行");
            return;
        }*/
        int pageNum = 1;
        int pageSize = 20;
        while (true) {

            LambdaQueryWrapper<MinerLongitudeLatitude> queryWrapper = new LambdaQueryWrapper();
            IPage<MinerLongitudeLatitude> page = minerLongitudeLatitudeService.page(new Page<>(pageNum, pageSize), queryWrapper);

            List<MinerLongitudeLatitude> batch = new ArrayList<>();
            for(MinerLongitudeLatitude v : page.getRecords()){
                if(v.getAddress().startsWith("中国")){
                    continue;
                }
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
            minerLongitudeLatitudeService.updateBatchById(batch);
            if (page.getRecords().size() < pageSize) {
                break;
            } else {
                pageNum ++;
            }
        }
        log.info("======================UpdateNodeAddressTask-end===================");
    }
}
