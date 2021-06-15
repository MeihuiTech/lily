package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

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
    @Autowired
    private ISysAggPowerDailyService sysAggPowerDailyService;

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

    @Test
    public  void CurrencyTest(){
        String date = DateUtils.getDate();
        String yesterDateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.addDays(DateUtils.parseDate(date), -1));
        System.out.print(yesterDateStr);
    }

    @Test
    public void AggTest(){
        //filusdt
        String result = HttpUtil.doPost("https://api.huobi.pro/market/history/kline?period=1min&size=1&symbol=xchusdt", "");
        log.info("请求响应值:{}",result);
        JSONObject json = JSONObject.parseObject(result);
        if("ok".equals(json.getString("status"))){
            JSONArray jsonArray = json.getJSONArray("data");
            JSONObject data = jsonArray.getJSONObject(0);
            BigDecimal high = data.getBigDecimal("high");
            BigDecimal low = data.getBigDecimal("low");
            BigDecimal open = data.getBigDecimal("open");
            BigDecimal close = data.getBigDecimal("close");

            log.info("fil今日价格:{}", high.add(low).add(open).add(close).divide(new BigDecimal(4)).doubleValue());
        }

       /* MarketClient marketClient = MarketClient.create(new HuobiOptions());

        String symbol = "btcusdt";

        List<Candlestick> list = marketClient.getCandlestick(CandlestickRequest.builder()
                .symbol(symbol)
                .interval(CandlestickIntervalEnum.MIN15)
                .size(10)
                .build());*/

    }

    @Test
    public void testNullJson(){
        System.out.print(JSON.toJSON(null));
    }

}
