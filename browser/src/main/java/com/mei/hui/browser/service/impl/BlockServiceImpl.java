package com.mei.hui.browser.service.impl;

import com.alibaba.fastjson.JSON;
import com.mei.hui.browser.common.Constants;
import com.mei.hui.browser.model.BlockPageListVO;
import com.mei.hui.browser.model.MinerAndBlock;
import com.mei.hui.browser.model.RankingBO;
import com.mei.hui.browser.service.BlockService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.PageResult;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.pipeline.BucketSortPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class BlockServiceImpl implements BlockService {
    @Autowired
    private RestHighLevelClient client;

    public PageResult<BlockPageListVO> blockPageList(@RequestBody RankingBO rankingBO) throws IOException{
        //查询es获取矿工对应的出块份数，并分页排序
        List<MinerAndBlock> minerAndBlocks = rankBlock(rankingBO.getRange(), rankingBO.getPageNum(), rankingBO.getPageSize());


        return null;
    }

    /**
     * 获取24小时出块份数,并分页
     */
    public List<MinerAndBlock> rankBlock(int range, long pageNum, long pageSize) throws IOException {
        long from = (pageNum - 1) * pageSize + 1;
        //0--24小时,1--7天,2--30天,3--1年
        long second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusHours(24));
        if(range == 1){
            second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusDays(7));
        }else if(range == 2){
            second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusDays(30));
        }else if(range == 3){
            second = DateUtils.localDateTimeToSecond(LocalDateTime.now().minusYears(1));
        }
        //查询es
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(0)
                .query(QueryBuilders.constantScoreQuery(QueryBuilders.rangeQuery("timestamp").gte(second)));
        builder.aggregation(
                AggregationBuilders.terms("group_by_minerId").field("miner")
                        .subAggregation(AggregationBuilders.sum("sum_win_count").field("win_count")
                        .subAggregation(new BucketSortPipelineAggregationBuilder("win_count_sort",Arrays.asList(
                                new FieldSortBuilder("sum_win_count").order(SortOrder.DESC)))
                                        .from(Integer.valueOf(from+""))
                                        .size(Integer.valueOf(pageSize+""))
                        )
                        )
        );
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(builder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("group_by_minerId");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        List<MinerAndBlock> list = new ArrayList<>();
        for(Terms.Bucket bucket : buckets){
            String minerId = bucket.getKeyAsString();
            Aggregations subAgg = bucket.getAggregations();
            Sum sum = subAgg.get("sum_win_count");
            double sumWinCount = sum.getValue();
            MinerAndBlock minerAndBlock = new MinerAndBlock()
                    .setMinerId(minerId)
                    .setBlockCount(Integer.valueOf(sumWinCount+""))
                    .setSort(from);
            list.add(minerAndBlock);
            from++;
        }
        return list;
    }

    /**
     * 24小时出块奖励
     * @return
     * @throws IOException
     */
    public Map<String, BigDecimal> twentyFourBlockIncr(int range,List<String> minerIds) throws IOException {
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
        sourceBuilder.query(QueryBuilders.boolQuery()
                .filter(QueryBuilders.termsQuery("miner",minerIds))
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
            Sum sumMoney = bucket.getAggregations().get("sum_money");
            map.put(minerId,new BigDecimal(sumMoney.getValue()+""));
        }
        log.info("24小时出块奖励:{}",JSON.toJSONString(map));
        return map;
    }

}
