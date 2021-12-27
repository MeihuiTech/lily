package com.mei.hui.browser.Controller;

import com.alibaba.fastjson.JSON;
import com.mei.hui.browser.model.BlockPageListVO;
import com.mei.hui.browser.model.PowerIncrRankVO;
import com.mei.hui.browser.model.PowerRankingVO;
import com.mei.hui.browser.model.RankingBO;
import com.mei.hui.browser.service.PowerService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "首页排行榜-有效算力")
@RestController
@RequestMapping("/power")
@Slf4j
public class PowerController {

    @Autowired
    private PowerService powerService;

    @ApiOperation(value = "有效算力排行榜")
    @PostMapping("/transferRecordDetail")
    public PageResult<PowerRankingVO> powerRanking(@RequestBody BasePage page) throws IOException {
        return powerService.powerRanking(page);
    }

    @ApiOperation(value = "算力增速排行榜")
    @PostMapping("/powerIncrRanking")
    public PageResult<PowerIncrRankVO> powerIncrRanking(@RequestBody RankingBO rankingBO) throws IOException {
        Map<String, BigDecimal> topMap = powerService.powerTwentyFourTopOrButtom(rankingBO.getRange(), null, true);
        log.info("24小时顶部算力:{}", JSON.toJSONString(topMap));
        Map<String, BigDecimal> buttomMap = powerService.powerTwentyFourTopOrButtom(rankingBO.getRange(), null, false);
        log.info("24小时底部算力:{}", JSON.toJSONString(topMap));
        List<PowerIncrRankVO> list = new ArrayList();
        for (String minerId : topMap.keySet()) {
            BigDecimal topPower = topMap.get(minerId);
            BigDecimal buttomPower = buttomMap.getOrDefault(minerId,BigDecimal.ZERO);
            BigDecimal twentyFourPower = BigDecimal.ZERO;
            if(topPower != null){
                twentyFourPower = topPower.subtract(buttomPower);
            }
            PowerIncrRankVO vo = new PowerIncrRankVO();
            vo.setMinerId(minerId);
            vo.setTwentyFourPower(twentyFourPower);
            list.add(vo);
        }
        Collections.sort(list, (o1, o2) -> {
            int value = o2.getTwentyFourPower().compareTo(o1.getTwentyFourPower());
            if(value > 0){
                return 1;
            }else{
                return -1;
            }
        });
        List<PowerIncrRankVO> rs = new ArrayList();
        long from = (rankingBO.getPageNum() - 1) * rankingBO.getPageSize();
        log.info("list size={},from:{},page:{}",list.size(),from,rankingBO.getPageSize());
        for(int n= new BigDecimal(from).intValue();n < from + rankingBO.getPageSize();n++){
            if(list.size() > n){
                PowerIncrRankVO vo = list.get(n);
                vo.setSort(n);
                rs.add(vo);
            }
        }
        List<String> minerIds = rs.stream().map(v -> v.getMinerId()).collect(Collectors.toList());
        //矿工有效算力
        Map<String, BigDecimal> powerAvailableMap = powerService.findMinerPower(minerIds);
        rs.stream().forEach(v->{
            v.setMinerPowerAvailable(powerAvailableMap.get(v.getMinerId()));
        });
        PageResult<PowerIncrRankVO> pageResult = new PageResult<>(list.size(),rs);
        return pageResult;
    }

}
