package com.mei.hui.browser.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mei.hui.browser.entity.BlockHeaders;
import org.springframework.stereotype.Repository;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:49
 **/
@Repository
public interface BlockHeadersMapper extends BaseMapper<BlockHeaders> {
    public String selectBlockHeadersCount();
}
