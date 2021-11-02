package com.mei.hui.browser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mei.hui.browser.common.Constants;
import com.mei.hui.browser.entity.FullTextSearchEntity;
import com.mei.hui.browser.service.FullTextSearchService;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.operators.relational.FullTextSearch;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/29 14:34
 **/
@Service
@Slf4j
public class FullTextSearchServiceImpl implements FullTextSearchService {

    @Autowired
    private RestHighLevelClient esClient;


    /*首页全站搜索*/
    @Override
    public String selectFullTextSearch(String searchText) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(Constants.ES_FULL_TEXT_INDEX);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 精确查询
        sourceBuilder.query(QueryBuilders.termQuery("query_string",searchText));
        request.source(sourceBuilder);
        SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);

        SearchHits hits = response.getHits();
        FullTextSearchEntity fullTextSearchEntity = new FullTextSearchEntity();
        for (SearchHit searchHit:hits){
            fullTextSearchEntity = JSONObject.parseObject(searchHit.getSourceAsString(),FullTextSearchEntity.class);
        }

        return fullTextSearchEntity.getType();
    }




}
