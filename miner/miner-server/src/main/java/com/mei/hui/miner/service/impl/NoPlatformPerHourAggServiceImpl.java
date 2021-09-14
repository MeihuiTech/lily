package com.mei.hui.miner.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.entity.NoPlatformMiner;
import com.mei.hui.miner.entity.NoPlatformPerHourAgg;
import com.mei.hui.miner.mapper.NoPlatformPerHourAggMapper;
import com.mei.hui.miner.service.NoPlatformMinerService;
import com.mei.hui.miner.service.NoPlatformPerHourAggService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.html.DateFormatEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 非平台矿工,每小时出块数 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-09-13
 */
@Service
@Slf4j
public class NoPlatformPerHourAggServiceImpl extends ServiceImpl<NoPlatformPerHourAggMapper, NoPlatformPerHourAgg>
        implements NoPlatformPerHourAggService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private NoPlatformMinerService noPlatformMinerService;

    /**
     * 如果聚合表没有记录，则返回该矿工的总出块数
     * @param minerId
     * @return
     */
    public Long getPreNoPlatformPerHourAgg(String minerId){
        LocalDateTime dateTime = LocalDateTime.now().withHour(1).withMinute(0).withSecond(0).withNano(0);
        String strDateTime = DateUtils.localDateTimeToString(dateTime, DateFormatEnum.YYYY_MM_DD_HH_MM_SS);
        //NoPlatform:minerId:2021-09-12 13:00:00
        String key = String.format("NoPlatform:%s:%s", minerId, strDateTime);
        String totalBlocks = redisUtil.get(key);
        log.info("key={},totalBlocks={}",key,totalBlocks);
        if(StringUtils.isNotEmpty(totalBlocks)){
            return Long.valueOf(totalBlocks);
        }else {
            Long blocks;
            LambdaQueryWrapper<NoPlatformPerHourAgg> lambdaQueryWrapper = new LambdaQueryWrapper();
            lambdaQueryWrapper.eq(NoPlatformPerHourAgg::getMinerId,minerId);
            lambdaQueryWrapper.eq(NoPlatformPerHourAgg::getCreateTime,strDateTime);
            NoPlatformPerHourAgg vo = this.getOne(lambdaQueryWrapper);
            if(vo != null){
                blocks = vo.getTotalBlocks();
            }else {
                //如果缓存和mysql都没有聚合信息，则返回矿工的总出块数
                NoPlatformMiner entity = noPlatformMinerService.getById(minerId);
                blocks = entity.getTotalBlocks();
            }
            redisUtil.set(key,blocks+"",3,TimeUnit.HOURS);
            return blocks;
        }
    }

}
