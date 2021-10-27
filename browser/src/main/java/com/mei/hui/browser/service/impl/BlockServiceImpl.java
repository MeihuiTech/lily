package com.mei.hui.browser.service.impl;

import com.mei.hui.browser.service.BlockService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BlockServiceImpl implements BlockService {
    @Autowired
    private RestHighLevelClient client;

}
