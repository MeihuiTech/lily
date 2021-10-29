package com.mei.hui.browser.service.impl;

import com.alibaba.fastjson.JSON;
import com.mei.hui.browser.common.Constants;
import com.mei.hui.browser.model.Block;
import com.mei.hui.browser.model.BlockPageListVO;
import com.mei.hui.browser.model.MinerAndBlock;
import com.mei.hui.browser.model.RankingBO;
import com.mei.hui.browser.service.BlockService;
import com.mei.hui.browser.service.FilExOverviewService;
import com.mei.hui.browser.service.PowerService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregatorBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.CardinalityAggregationBuilder;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlockServiceImpl implements BlockService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private PowerService powerService;
    @Autowired
    private FilExOverviewService filExOverviewService;

    public PageResult<BlockPageListVO> blockPageList(@RequestBody RankingBO rankingBO) throws IOException{
        int range = rankingBO.getRange();
        //查询es获取矿工对应的出块份数，并分页排序
        Block block = rankBlock(range, rankingBO.getPageNum(), rankingBO.getPageSize());
        List<String> minerIds = block.getList().stream().map(v -> v.getMinerId()).collect(Collectors.toList());
        //获取矿工24小时算力增量
        Map<String, BigDecimal> powerMap = powerService.twentyFourPowerIncr(range, minerIds);
        //获取矿工的有效算力
        Map<String, BigDecimal> availablePowerMap = powerService.findMinerPower(minerIds);
        //全网总有效算力
        BigDecimal totalQaBytesPower = filExOverviewService.list().get(0).getTotalQaBytesPower();
        List<BlockPageListVO> list = new ArrayList<>();
        for(MinerAndBlock b : block.getList()){
            BigDecimal availablePower = availablePowerMap.get(b.getMinerId());
            BlockPageListVO vo = new BlockPageListVO();
            vo.setSort(b.getSort());
            vo.setMinerId(b.getMinerId());
            vo.setBlockCount(b.getBlockCount());
            vo.setTwentyFourBlockAward(b.getTwentyFourBlockAward());
            vo.setTwentyFourTotalBlockAward(block.getTwentyFourTotalBlockAward());
            vo.setMinerPowerAvailable(availablePower);
            vo.setTotalPowerAvailable(totalQaBytesPower);
            list.add(vo);
        }
        PageResult<BlockPageListVO> pageResult = new PageResult<>(block.getCount(),list);
        return pageResult;
    }

    /**
     * 获取24小时出块份数,并分页
     */
    public Block rankBlock(int range, long pageNum, long pageSize) throws IOException {
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
        TermsAggregationBuilder groupByMinerId = AggregationBuilders.terms("group_by_minerId").field("miner").size(Integer.valueOf(from + pageSize+""));
        groupByMinerId.subAggregation(AggregationBuilders.sum("sum_win_count").field("win_count"));
        groupByMinerId.subAggregation(new BucketSortPipelineAggregationBuilder("win_count_sort",Arrays.asList(
                new FieldSortBuilder("sum_win_count").order(SortOrder.DESC)))
                .from(Integer.valueOf(from+""))
                .size(Integer.valueOf(pageSize+""))
        );
        //24小时出块奖励
        groupByMinerId.subAggregation(AggregationBuilders.sum("twentyFourBlockAward").field("money"));
        builder.size(0).query(QueryBuilders.constantScoreQuery(QueryBuilders.rangeQuery("timestamp").gte(second)));
        builder.aggregation(groupByMinerId)
               .aggregation(AggregationBuilders.cardinality("miner_count").field("miner"))
               .aggregation(AggregationBuilders.sum("twentyFourTotalBlockAward").field("money"));
        SearchRequest searchRequest = new SearchRequest(Constants.ES_BLOCK_INDEX);
        searchRequest.source(builder);
        log.info("获取24小时出块份数,DSL:{}",searchRequest.source().toString());
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("group_by_minerId");
        Sum twentyFourTotalBlockAwardSum = response.getAggregations().get("twentyFourTotalBlockAward");
        Cardinality cardinality = response.getAggregations().get("miner_count");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        List<MinerAndBlock> list = new ArrayList<>();
        for(Terms.Bucket bucket : buckets){
            String minerId = bucket.getKeyAsString();
            Aggregations subAgg = bucket.getAggregations();
            Sum sum = subAgg.get("sum_win_count");
            double sumWinCount = sum.getValue();
            Sum twentyFourBlockAwardSum = subAgg.get("twentyFourBlockAward");
            double twentyFourBlockAward = twentyFourBlockAwardSum.getValue();
            MinerAndBlock minerAndBlock = new MinerAndBlock()
                    .setMinerId(minerId)
                    .setBlockCount(new BigDecimal(sumWinCount).intValue())
                    .setSort(from)
                    .setTwentyFourBlockAward(new BigDecimal(twentyFourBlockAward));
            list.add(minerAndBlock);
            from++;
        }
        Block block = new Block();
        block.setTwentyFourTotalBlockAward(new BigDecimal(twentyFourTotalBlockAwardSum.getValue()));
        block.setList(list);
        block.setCount(cardinality.getValue());
        log.info("获取24小时矿工出块份数:{}",JSON.toJSONString(block));
        return block;
    }

    /**
     * 24小时出块奖励增量
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
        log.info("24小时出块奖励增量:{}",JSON.toJSONString(map));
        return map;
    }

}
