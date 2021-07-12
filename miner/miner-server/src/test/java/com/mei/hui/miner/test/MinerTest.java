package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.entity.FilBaselinePowerDayAgg;
import com.mei.hui.miner.entity.FilReportGas;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.service.FilBaselinePowerDayAggService;
import com.mei.hui.miner.service.FilReportGasService;
import com.mei.hui.miner.service.ISysAggPowerDailyService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SysUserOut;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private FilBaselinePowerDayAggService baselinePowerDayAggService;
    @Test
    public void saveTest(){
        FilBaselinePowerDayAgg baselinePowerDayAgg = new FilBaselinePowerDayAgg();
        baselinePowerDayAgg.setBaseLine(new BigDecimal(12))
                .setBlocks(2L)
                .setCreateTime(LocalDateTime.now())
                .setPower(new BigDecimal(2000))
                .setDate(LocalDate.now());
        baselinePowerDayAggService.save(baselinePowerDayAgg);
    }

    @Test
    public void getTest(){
        LambdaQueryWrapper<FilBaselinePowerDayAgg> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FilBaselinePowerDayAgg::getId,1);
        List<FilBaselinePowerDayAgg> list = baselinePowerDayAggService.list(queryWrapper);
        log.info(JSON.toJSONString(list));
    }

    @Test
    public void copyTest(){
        FilBaselinePowerDayAgg baselinePowerDayAgg = new FilBaselinePowerDayAgg();
        baselinePowerDayAgg.setBaseLine(new BigDecimal(12))
                .setBlocks(2L)
                .setCreateTime(LocalDateTime.now())
                .setPower(new BigDecimal(2000))
                .setDate(LocalDate.now());
        FilBaselinePowerDayAgg DayAgg = new FilBaselinePowerDayAgg();

        BeanUtils.copyProperties(baselinePowerDayAgg,DayAgg);

        log.info("复制:{}",JSON.toJSONString(DayAgg));

    }

    @Test
    public void testLong() {
        System.out.println(0L-0L);
    }

    @Test
    public  void testFindAllUser(){
        Result<List<SysUserOut>> result = userFeignClient.findAllUser();
        if(!ErrorCode.MYB_000000.getCode().equals(result.getCode())){
            throw MyException.fail(result.getCode(),result.getMsg());
        }
        System.out.print("查询所有用户:【{}】"+JSON.toJSON(result.getData()));
    }

    @Test
    public void testPath(){
        System.out.println(System.getProperty("user.dir")+"\\src\\main\\resources\\swagger.txt");
    }


    @Test
    public void testLocalDateTime(){
        System.out.println(LocalDateTime.now());
        System.out.println(LocalDateTime.now().plusHours(-24L));
    }

}
