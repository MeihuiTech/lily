package com.mei.hui.browser.service.impl;

import com.mei.hui.browser.common.Constants;
import com.mei.hui.browser.model.BlockRankingBO;
import com.mei.hui.browser.model.PowerRankingVO;
import com.mei.hui.browser.service.BlockService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BlockServiceImpl implements BlockService {
    @Autowired
    private RestHighLevelClient client;


    /**
     * 24小时出块
     * @return
     * @throws IOException
     */
    public Map<String, BigDecimal> blockRanking(int range,List<String> minerIds) throws IOException {

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
        sourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.termsQuery("miner_id",minerIds))
                .filter(QueryBuilders.rangeQuery("timestamp").gte(second)));
        sourceBuilder.aggregation(
                AggregationBuilders.terms("group_by_minerId").field("miner")
                        .subAggregation(AggregationBuilders.sum("sum_money").field("money"))
        );
        SearchRequest searchRequest = new SearchRequest(Constants.ES_BLOCK_INDEX);
        searchRequest.source(sourceBuilder);
        log.info("获取24小时出块DSL:{}",searchRequest.source().toString());
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("group_by_minerId");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        Map<String, BigDecimal> map = new HashMap<>();
        for(Terms.Bucket bucket : buckets){
            String minerId = bucket.getKeyAsString();
            //最后一条算力记录
            Sum sumMoney = bucket.getAggregations().get("sum_money");
            map.put(minerId,new BigDecimal(sumMoney.getValue()+""));
        }
        return map;
    }

}
