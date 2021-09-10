package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.config.AESUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication .class)
@Log4j2
public class MinerTest {


    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;


    @Test
    public void entry(){
        LambdaQueryWrapper<SysMinerInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SysMinerInfo::getMinerId,"f01016361");
        List<SysMinerInfo> list = sysMinerInfoMapper.selectList(lambdaQueryWrapper);
        log.info("token = {}", JSON.toJSONString(list));
    }


}
