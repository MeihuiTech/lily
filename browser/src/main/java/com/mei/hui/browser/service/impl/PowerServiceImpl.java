package com.mei.hui.browser.service.impl;

import com.mei.hui.browser.common.Constants;
import com.mei.hui.browser.model.Miner;
import com.mei.hui.browser.model.MinerPower;
import com.mei.hui.browser.model.PowerRankingVO;
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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.TopHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PowerServiceImpl implements PowerService {

    private final int size = 10;

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private FilExOverviewService filExOverviewService;

    public PageResult<PowerRankingVO> powerRanking(BasePage page) throws IOException {
        /**
         * 获取矿工信息
         */
        Miner miners = findMiners(page);
        List<String> minerIds = miners.getList().stream().map(v -> v.getMinerId()).collect(Collectors.toList());
        /**
         * 查询
         */
        long second = DateUtils.localDateTimeToSecond(LocalDateTime.now());
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.termsQuery("miner_id",minerIds))
                .filter(QueryBuilders.rangeQuery("timestamp").gte(second)));

        sourceBuilder.aggregation(
                AggregationBuilders.terms("group_by_minerId").field("miner_id")
                .subAggregation(AggregationBuilders.topHits("max_hit")
                        .size(1)
                        .fetchSource(new String[]{"quality_adj_power","height","timestamp"},new String[]{})
                        .sort("height",SortOrder.DESC))

                .subAggregation(AggregationBuilders.topHits("min_hit")
                .size(1)
                .fetchSource(new String[]{"quality_adj_power","height","timestamp"},new String[]{})
                .sort("height",SortOrder.ASC))
        );
        SearchRequest searchRequest = new SearchRequest(Constants.ES_POWER_INDEX);
        searchRequest.source(sourceBuilder);
        log.info("DSL:{}",searchRequest.source().toString());
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
            Long maxPower = (Long) maxSource.get("quality_adj_power");

            //最小算力记录
            TopHits minHit = bucket.getAggregations().get("max_hit");
            SearchHit minSearchHit = maxHit.getHits().getHits()[0];
            Map<String, Object> minSource = maxSearchHit.getSourceAsMap();
            Long minPower = (Long) minSource.get("quality_adj_power");
            map.put(minerId,new BigDecimal(maxPower).subtract(new BigDecimal(minPower)));
        }
        //全网总有效算力
        BigDecimal totalQaBytesPower = filExOverviewService.list().get(0).getTotalQaBytesPower();
        List<PowerRankingVO> list = miners.getList().stream().map(v -> {
            PowerRankingVO vo = new PowerRankingVO();
            vo.setMinerId(v.getMinerId());
            BigDecimal twentyFourPower = map.get(v.getMinerId());
            if (twentyFourPower == null) {
                twentyFourPower = BigDecimal.ZERO;
            }
            vo.setTwentyFourPower(twentyFourPower);
            vo.setTotalPowerAvailable(totalQaBytesPower);
            return vo;
        }).collect(Collectors.toList());
        PageResult pageResult = new PageResult(miners.getTotal(),list);
        return pageResult;
    }

    /**
     *获取存储基本数据
     * @return
     */
    public Miner findMiners(BasePage page) throws IOException {
        long from = (page.getPageNum() - 1) * page.getPageSize() + 1;
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

        List<MinerPower> list = Arrays.stream(hits).map(v->{
            Map<String, Object> map = v.getSourceAsMap();
            String minerId = (String) map.get("miner_id");
            String powerAvailable = (String) map.get("quality_adj_power");
            MinerPower minerPower = new MinerPower();
            minerPower.setMinerId(minerId);
            minerPower.setPowerAvailable(new BigDecimal(powerAvailable));
            return  minerPower;
        }).collect(Collectors.toList());
        Miner miner = new Miner();
        miner.setList(list);
        miner.setTotal(total);
        return miner;
    }
}
