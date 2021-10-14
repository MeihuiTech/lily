package com.mei.hui.browser.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.browser.entity.BlockHeaders;
import com.mei.hui.browser.mapper.BlockHeadersMapper;
import com.mei.hui.browser.service.BlockHeadersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:56
 **/
@Service
@Slf4j
public class BlockHeadersServiceImpl extends ServiceImpl<BlockHeadersMapper,BlockHeaders>  implements BlockHeadersService{

    @Autowired
    private BlockHeadersMapper blockHeadersMapper;

    @Override
    public String selectBlockHeadersCount() {
        return blockHeadersMapper.selectBlockHeadersCount();
    }
}
