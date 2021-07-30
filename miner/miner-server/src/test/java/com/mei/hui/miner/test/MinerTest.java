package com.mei.hui.miner.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.miner.MinerApplication;
import com.mei.hui.miner.SystemController.FilReportedController;
import com.mei.hui.miner.entity.FilBaselinePowerDayAgg;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.model.RequestSectorInfo;
import com.mei.hui.miner.service.*;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ISysSectorsWrapService sysSectorsWrapService;

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

    /**
     * 将域名转换为ip地址
     * @throws UnknownHostException
     */
    @Test
    public void testAddressToIp() throws UnknownHostException {
//获取本机IP地址
        System.out.println(InetAddress.getLocalHost().getHostAddress());
//获取www.luoruiyuan.cn的地址
        System.out.println(InetAddress.getByName("www.luoruiyuan.cn").getHostAddress());
//获取www.luoruiyuan.cn的真实IP地址
        System.out.println(InetAddress.getByName("www.luoruiyuan.cn"));
//获取配置在HOST中的域名IP地址
        System.out.println(InetAddress.getByName("www.luoruiyuan.cn").getHostAddress());
    }


    @Test
    public void testIpToAddress(){
        String rt = HttpUtil.doGet("https://restapi.amap.com/v5/ip?ip=193.118.43.158&key=a86d26bdd2ab3fcba6251ae6bb974c43&type=4","");
        System.out.println(rt);
    }

    @Autowired
    private ISysSectorInfoService sysSectorInfoService;

    @Test
    public void testSectorDuration(){
        RequestSectorInfo sysSectorInfo = new RequestSectorInfo();
        sysSectorInfo.setMinerId("f01016365");
        sysSectorInfo.setSectorNo(25631L);
        sysSectorInfo.setSectorStatus(5);
        SysSectorInfo sectorInfo = sysSectorInfoService.selectSysSectorInfoByMinerIdAndSectorNoAndStatus(sysSectorInfo);
        log.info("----------------------------【{}】",JSON.toJSON(sectorInfo));
    }

    /**
     * 测试mybatisplus分页限制500条数据
     */
    @Test
    public void testMyBatisPlusPage(){
        IPage page = sysSectorsWrapService.page(new Page(1,2000));
        System.out.println(page.getRecords().size());
    }

    @Autowired
    private FilReportedController filReportedController;

    /**
     * 调用上报fil新增扇区接口
     */
    @Test
    public void testFilReportedSector(){
        String url = "http://122.9.63.92/prod-api/miner-server/fil/reported/sector";
        Map<String,String> header = new HashMap<>();
        header.put("x-api-key","97067dddaa11473bafb8dd8bded87ff9");
        List<String> jsonList = new ArrayList<>();
        /*jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":1,\"time\":\"2021-07-18T17:52:02\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":1,\"time\":\"2021-07-18T17:52:53\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":2,\"time\":\"2021-07-19T07:12:23\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":2,\"time\":\"2021-07-19T13:10:15\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":3,\"time\":\"2021-07-19T15:12:41\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":3,\"time\":\"2021-07-19T15:33:00\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":4,\"time\":\"2021-07-19T15:36:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":4,\"time\":\"2021-07-19T15:51:30\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":5,\"time\":\"2021-07-19T15:51:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":5,\"time\":\"2021-07-19T15:51:33\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":6,\"time\":\"2021-07-19T15:51:33\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":6,\"time\":\"2021-07-19T17:01:20\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":7,\"time\":\"2021-07-19T17:04:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":7,\"time\":\"2021-07-19T17:07:47\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":8,\"time\":\"2021-07-19T17:04:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":8,\"time\":\"2021-07-19T17:07:49\"}");*/
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":1,\"time\":\"2021-07-18T17:52:02\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":1,\"time\":\"2021-07-18T17:52:53\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":2,\"time\":\"2021-07-19T07:12:23\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":2,\"time\":\"2021-07-19T13:10:15\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":3,\"time\":\"2021-07-19T15:12:41\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":3,\"time\":\"2021-07-19T15:33:00\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":4,\"time\":\"2021-07-19T15:36:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":4,\"time\":\"2021-07-19T15:51:30\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":5,\"time\":\"2021-07-19T15:51:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":5,\"time\":\"2021-07-19T15:51:33\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":6,\"time\":\"2021-07-19T15:51:33\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":6,\"time\":\"2021-07-19T17:01:20\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":7,\"time\":\"2021-07-19T17:04:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":7,\"time\":\"2021-07-19T17:07:47\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":5,\"time\":\"2021-07-19T17:08:47\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":5,\"time\":\"2021-07-19T17:09:47\"}");
        jsonList.add("{\"action\":\"start\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":8,\"time\":\"2021-07-19T17:10:30\"}");
        jsonList.add("{\"action\":\"stop\",\"hostname\":\"workerP-10-10-65-78\",\"minerId\":\"f01016365\",\"pageNum\":1,\"pageSize\":10,\"sectorNo\":25631,\"sectorSize\":68719476736,\"sectorStatus\":8,\"time\":\"2021-07-19T17:11:49\"}");
        for (String json : jsonList){
            String result = HttpUtil.doPost(url,json,header);
            System.out.println("------------"+result);
        }
    }

    public static void main(String[] args) {
        buy(new BigDecimal("67.05"),1,new BigDecimal("61.96"));
    }


    /**
     *
     * @param price 上次购买时的单价
     * @param shou 上次购买手数
     * @param currentPrice 当前单价
     */
    public static void buy(BigDecimal price,int shou,BigDecimal currentPrice){

        for(int i=1;i<=10;i++){

            //已买总额
            BigDecimal p = price.multiply(new BigDecimal(shou * 100));
            BigDecimal c = currentPrice.multiply(new BigDecimal(100 * i));

            //购买后每股的价钱
            BigDecimal houPrice = c.add(p).divide(new BigDecimal((shou + i) * 100),4, BigDecimal.ROUND_HALF_UP);

            log.info("当前价钱:{},购买数量:{},需要花费:{},购买后股价:{}",currentPrice,100*i,c.toPlainString(),houPrice.toPlainString());
            if(i == 10){
                break;
            }

        }

    }


}
