package com.mei.hui.miner.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.HttpUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.ChiaMiner;
import com.mei.hui.miner.entity.SysAggAccountDaily;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.GetAssetRateBO;
import com.mei.hui.miner.feign.vo.GetMonyRateVO;
import com.mei.hui.miner.manager.UserManager;
import com.mei.hui.miner.mapper.ChiaMinerMapper;
import com.mei.hui.miner.mapper.SysAggAccountDailyMapper;
import com.mei.hui.miner.mapper.SysMinerInfoMapper;
import com.mei.hui.miner.model.XchMinerDetailBO;
import com.mei.hui.miner.service.ISysAggAccountDailyService;
import com.mei.hui.util.CurrencyEnum;
import com.mei.hui.util.BigDecimalUtil;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 账户按天聚合Service业务层处理
 * 
 * @author ruoyi
 * @date 2021-04-06
 */
@Slf4j
@Service
public class SysAggAccountDailyServiceImpl implements ISysAggAccountDailyService
{
    @Autowired
    private SysAggAccountDailyMapper sysAggAccountDailyMapper;
    @Autowired
    private UserManager userManager;
    @Autowired
    private SysMinerInfoMapper sysMinerInfoMapper;
    @Autowired
    private ChiaMinerMapper chiaMinerMapper;

    /**
     * 查询账户按天聚合
     *
     * @param id 账户按天聚合ID
     * @return 账户按天聚合
     */
    @Override
    public SysAggAccountDaily selectSysAggAccountDailyById(Long id)
    {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyById(id);
    }

    @Override
    public SysAggAccountDaily selectSysAggAccountDailyByMinerIdAndDate(String minerId, String date) {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyByMinerIdAndDate(minerId, date);
    }

    @Override
    public List<SysAggAccountDaily> selectSysAggAccountDailyByMinerId(String minerId, String begin, String end,String type) {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyByMinerId(minerId, begin, end, type);
    }

    /**
     * 查询账户按天聚合列表
     *
     * @param sysAggAccountDaily 账户按天聚合
     * @return 账户按天聚合
     */
    @Override
    public List<SysAggAccountDaily> selectSysAggAccountDailyList(SysAggAccountDaily sysAggAccountDaily)
    {
        return sysAggAccountDailyMapper.selectSysAggAccountDailyList(sysAggAccountDaily);
    }

    /**
     * 新增账户按天聚合
     *
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    @Override
    public int insertSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily)
    {
        sysAggAccountDaily.setCreateTime(LocalDateTime.now());
        return sysAggAccountDailyMapper.insertSysAggAccountDaily(sysAggAccountDaily);
    }

    /**
     * 修改账户按天聚合
     *
     * @param sysAggAccountDaily 账户按天聚合
     * @return 结果
     */
    @Override
    public int updateSysAggAccountDaily(SysAggAccountDaily sysAggAccountDaily)
    {
        sysAggAccountDaily.setUpdateTime(LocalDateTime.now());
        return sysAggAccountDailyMapper.updateSysAggAccountDaily(sysAggAccountDaily);
    }

    /**
     * 批量删除账户按天聚合
     *
     * @param ids 需要删除的账户按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggAccountDailyByIds(Long[] ids)
    {
        return sysAggAccountDailyMapper.deleteSysAggAccountDailyByIds(ids);
    }

    /**
     * 删除账户按天聚合信息
     *
     * @param id 账户按天聚合ID
     * @return 结果
     */
    @Override
    public int deleteSysAggAccountDailyById(Long id)
    {
        return sysAggAccountDailyMapper.deleteSysAggAccountDailyById(id);
    }

    /**
     * 计算每个币种的资产占比
     * @param getAssetRateBO
     * @return
     */
    public Result<List<GetMonyRateVO>> getAssetRate(GetAssetRateBO getAssetRateBO){
        Long userId = getAssetRateBO.getUserId();
        /**
         * 校验userId 是否正确
         */
        userManager.checkUserIsExist(userId);
        /**
         * 一、获取fil币的总数量
         * 二、调用火币接口获取fil币的报价
         */
        //查询fil币总数量
        LambdaQueryWrapper<SysMinerInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(SysMinerInfo::getUserId,userId);
        log.info("获取fil币，入参:userId={}",userId);
        List<SysMinerInfo> filMIners = sysMinerInfoMapper.selectList(lambdaQueryWrapper);
        log.info("获取fil币，出参:{}", JSON.toJSONString(filMIners));
        BigDecimal filMony = new BigDecimal(0);
        for(SysMinerInfo minerInfo : filMIners){
            filMony = filMony.add(minerInfo.getBalanceMinerAccount());
        }
        log.info("fil币的总数量:{}",filMony.doubleValue());
        //获取fil币的报价
        BigDecimal filPrice = getUdst("filusdt");
        log.info("fil币的报价：{}",filPrice.doubleValue());
        /**
         * 一、获取起亚币的数量
         * 二、获取起亚币的报价
         */
        //查询起亚币
        LambdaQueryWrapper<ChiaMiner> chiaQuery = new LambdaQueryWrapper<>();
        chiaQuery.eq(ChiaMiner::getUserId,userId);
        log.info("获取chia币，入参:userId={}",userId);
        List<ChiaMiner> chiaMiners = chiaMinerMapper.selectList(chiaQuery);
        log.info("获取chia币，出参:{}",JSON.toJSONString(chiaMiners));
        BigDecimal chiaMony = new BigDecimal(0);
        for(ChiaMiner chiaMiner : chiaMiners){
            chiaMony = chiaMony.add(chiaMiner.getBalanceMinerAccount());
        }
        log.info("chia币的总数量:{}",chiaMony);
        //获取chia币的报价
        BigDecimal chiaPrice = getUdst("xchusdt");
        log.info("chia币的报价：{}",chiaPrice.doubleValue());

        //fil币的美元资产
        BigDecimal filUsdt = filMony.multiply(filPrice);
        log.info("fil币的美元资产:{}",filUsdt.doubleValue());
        //chia币的美元资产
        BigDecimal chiaUsdt = chiaMony.multiply(chiaPrice);
        log.info("chia币的美元资产:{}",filUsdt.doubleValue());

        BigDecimal totalAsset = filUsdt.add(chiaUsdt);
        log.info("总资产:{}",totalAsset);

        BigDecimal filRate = new BigDecimal(0);
        BigDecimal chiaRate = new BigDecimal(0);
        if(totalAsset.compareTo(new BigDecimal(0)) > 0){
             filRate = filUsdt.divide(totalAsset,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
             chiaRate = chiaUsdt.divide(totalAsset,4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));;
        }
        log.info("fil币资产占比：{}",filRate.doubleValue());
        log.info("chia币资产占比：{}",chiaRate.doubleValue());
        List<GetMonyRateVO> list = new ArrayList<>();
        //fil
        GetMonyRateVO fil = new GetMonyRateVO();
        fil.setRate(BigDecimalUtil.formatTwo(filRate));
        fil.setType(CurrencyEnum.FIL.name());
        list.add(fil);
        //chia
        GetMonyRateVO chia = new GetMonyRateVO();
        chia.setRate(BigDecimalUtil.formatTwo(chiaRate));
        chia.setType(CurrencyEnum.XCH.name());
        list.add(chia);

        return Result.success(list);
    }

    /**
     * 获取报价 xchusdt,filusdt
     * @param symbol
     * @return
     */
    public BigDecimal getUdst(String symbol){
        String url = "https://api.huobi.pro/market/history/kline?period=1min&size=1&symbol="+symbol;
        String result = HttpUtil.doPost(url,"");
        log.info("请求响应值:{}",result);
        JSONObject json = JSONObject.parseObject(result);
        if(!"ok".equals(json.getString("status"))){
            throw MyException.fail(MinerError.MYB_222222.getCode(),"获取报价失败");
        }
        JSONArray jsonArray = json.getJSONArray("data");
        JSONObject data = jsonArray.getJSONObject(0);
        BigDecimal high = data.getBigDecimal("high");
        BigDecimal low = data.getBigDecimal("low");
        BigDecimal open = data.getBigDecimal("open");
        BigDecimal close = data.getBigDecimal("close");
        BigDecimal price = high.add(low).add(open).add(close).divide(new BigDecimal(4));
        log.info("fil今日价格:{}",price);
        return price;
    }
}

