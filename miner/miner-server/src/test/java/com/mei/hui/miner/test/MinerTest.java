package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication .class)
@Slf4j
public class MinerTest {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;
    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;

    @Autowired
    private UserFeignClient userFeignClient;


    @Test
    public void entry(){
        String token = AESUtil.encrypt("1");
        log.info("token = {}",token);
    }

    @Test
    public void testRedis() {
        redisUtil.set("testRedisKey","testRedisValue");
        System.out.print(redisUtil.get("testRedisKey"));
    }
    @Test
    public void testMydql(){
        SysMinerInfo miner = sysMinerInfoMapper.selectById(29);
        miner.setDeadlineIndex(100000L);
        miner.setDeadlineSectors(10000L);
        miner.setProvingPeriodStart(100000L);
        miner.setId(30L);
       // sysMinerInfoService.updateSysMinerInfo(miner);
        sysMinerInfoService.insertSysMinerInfo(miner);
    }

    @Test
    public  void testGetUserById(){
        SysUserOut sysUserOut = new SysUserOut();
        sysUserOut.setUserId(5L);
        Result<SysUserOut> sysUserOutResult = userFeignClient.getUserById(sysUserOut);
        System.out.print(JSON.toJSON(sysUserOutResult));
    }


}
