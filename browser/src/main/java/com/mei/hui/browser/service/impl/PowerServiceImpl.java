package com.mei.hui.browser.service.impl;

import com.alibaba.fastjson.JSON;
import com.mei.hui.browser.common.Constants;
import com.mei.hui.browser.model.Miner;
import com.mei.hui.browser.model.MinerPower;
import com.mei.hui.browser.model.PowerRankingVO;
import com.mei.hui.browser.service.BlockService;
import com.mei.hui.browser.service.FilExOverviewService;
import com.mei.hui.browser.service.PowerService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PowerServiceImpl implements PowerService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private BlockService blockService;
    @Autowired
    private FilExOverviewService filExOverviewService;

    public PageResult<PowerRankingVO> powerRanking(BasePage page) throws IOException {
        /**
         * 获取矿工信息
         */
        Miner miners = pageListMiners(page);
        List<String> minerIds = miners.getList().stream().map(v -> v.getMinerId()).collect(Collectors.toList());

        //24小时算力增长
        Map<String, BigDecimal> minerPowerMap = twentyFourPowerIncr(0,minerIds);
        //24小时出块奖励
        Map<String, BigDecimal> minerBlockMap = blockService.twentyFourBlockIncr(0, minerIds);
        //全网总有效算力
        BigDecimal totalQaBytesPower = filExOverviewService.list().get(0).getTotalQaBytesPower();
        List<PowerRankingVO> list = miners.getList().stream().map(v -> {
            PowerRankingVO vo = new PowerRankingVO();
            vo.setMinerPowerAvailable(v.getPowerAvailable());
            vo.setMinerId(v.getMinerId());
            BigDecimal twentyFourPower = minerPowerMap.get(v.getMinerId());
            if (twentyFourPower == null) {
                twentyFourPower = BigDecimal.ZERO;
            }
            vo.setTwentyFourPower(twentyFourPower);
            vo.setTotalPowerAvailable(totalQaBytesPower);
            vo.setSort(v.getSort());

            BigDecimal twentyFourBlockAward = minerBlockMap.get(v.getMinerId());
            vo.setTwentyFourBlockAward(twentyFourBlockAward);
            return vo;
        }).collect(Collectors.toList());
        PageResult pageResult = new PageResult(miners.getTotal(),list);
        return pageResult;
    }

    /**
     *分页获取矿工
     * @return
     */
    public Miner pageListMiners(BasePage page) throws IOException {
        long from = (page.getPageNum() - 1) * page.getPageSize();
        SearchRequest searchRequest = new SearchRequest(Constants.ES_POWER_LATEST_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.from(Integer.valueOf(from + ""));
        builder.size(Integer.valueOf(page.getPageSize()+""));
        builder.query(QueryBuilders.rangeQuery("quality_adj_power").gt(0));
        builder.sort("quality_adj_power",SortOrder.DESC);
        searchRequest.source(builder);
        log.info("获取矿工基础信息,DSL:{}",searchRequest.source().toString());
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();

        List<MinerPower> list = new ArrayList<>();
        for(SearchHit v : hits){
            Map<String, Object> map = v.getSourceAsMap();
            String minerId = (String) map.get("miner_id");
            String powerAvailable = (String) map.get("quality_adj_power");
            MinerPower minerPower = new MinerPower();
            minerPower.setMinerId(minerId);
            minerPower.setPowerAvailable(new BigDecimal(powerAvailable));
            minerPower.setSort(from);
            list.add(minerPower);
            from++;
        }
        Miner miner = new Miner();
        miner.setList(list);
        miner.setTotal(total);
        log.info("获取矿工算力排行榜:{}",JSON.toJSONString(miner));
        return miner;
    }

    /**
     * 矿工24小时算力增长
     * @param minerIds
     */
    public Map<String,BigDecimal> twentyFourPowerIncr(int range,List<String> minerIds) throws IOException {
        Map<String, BigDecimal> topMap = powerTwentyFourTopOrButtom(range, minerIds, true);
        log.info("24小时顶部算力:{}", JSON.toJSONString(topMap));
        Map<String, BigDecimal> buttomMap = powerTwentyFourTopOrButtom(range, minerIds, false);
        log.info("24小时底部算力:{}", JSON.toJSONString(topMap));
        Map<String, BigDecimal> map = new HashMap<>();
        for (String minerId : minerIds) {
            BigDecimal topPower = topMap.get(minerId);
            BigDecimal buttomPower = buttomMap.getOrDefault(minerId,BigDecimal.ZERO);
            BigDecimal twentyFourPower = BigDecimal.ZERO;
            if(topPower != null){
                twentyFourPower = topPower.subtract(buttomPower);
            }
            map.put(minerId,twentyFourPower);
        }
        return map;
    }

    /**
     * 获取24小时顶部算力，相减即是24小时的算力增速, type=0 顶部；type=1底部
     * @return
     */
    public Map<String,BigDecimal> powerTwentyFourTopOrButtom(int range,List<String> minerIds,boolean isTop) throws IOException {
        //0--24小时,1--7天,2--30天,3--1年
        long second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusHours(24));
        if(range == 1){
            second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusDays(7));
        }else if(range == 2){
            second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusDays(30));
        }else if(range == 3){
            second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusYears(1));
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(0);
        if(isTop){
            sourceBuilder.query(QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termsQuery("miner_id",minerIds))
                    .filter(QueryBuilders.rangeQuery("timestamp").gte(second)));
        }else{
            sourceBuilder.query(QueryBuilders.boolQuery()
                    .filter(QueryBuilders.termsQuery("miner_id",minerIds))
                    .filter(QueryBuilders.rangeQuery("timestamp").lte(second)));
        }
        sourceBuilder.aggregation(
                AggregationBuilders.terms("group_by_minerId").field("miner_id")
                        .subAggregation(AggregationBuilders.topHits("max_hit")
                                .size(1)
                                .sort("height",SortOrder.DESC))
        );
        SearchRequest searchRequest = new SearchRequest(Constants.ES_POWER_INDEX);
        searchRequest.source(sourceBuilder);
        log.info("24小时顶部算力,DSL:{}",searchRequest.source().toString());
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("group_by_minerId");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        Map<String,BigDecimal> map = new HashMap<>();
        for(Terms.Bucket bucket : buckets){
            String minerId = bucket.getKeyAsString();
            //最后一条算力记录
            TopHits maxHit = bucket.getAggregations().get("max_hit");
            SearchHit maxSearchHit = maxHit.getHits().getHits()[0];
            Map<String, Object> maxSource = maxSearchHit.getSourceAsMap();
            String maxPower = (String) maxSource.get("quality_adj_power");
            map.put(minerId,new BigDecimal(maxPower));
        }
        return map;
    }

    /**
     * 获取矿工有效算力
     * @param minerIds
     * @return
     * @throws IOException
     */
    public Map<String,BigDecimal> findMinerPower(List<String> minerIds) throws IOException {
        SearchRequest searchRequest = new SearchRequest(Constants.ES_POWER_LATEST_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.constantScoreQuery(QueryBuilders.termsQuery("miner_id",minerIds)));
        searchRequest.source(builder);
        log.info("获取矿工基础信息,DSL:{}",searchRequest.source().toString());
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        Map<String,BigDecimal> minerMap = new HashMap<>();
        for(SearchHit v : hits){
            Map<String, Object> map = v.getSourceAsMap();
            String minerId = (String) map.get("miner_id");
            String powerAvailable = (String) map.get("quality_adj_power");
            minerMap.put(minerId,new BigDecimal(powerAvailable));
        }
        return minerMap;
    }
}
