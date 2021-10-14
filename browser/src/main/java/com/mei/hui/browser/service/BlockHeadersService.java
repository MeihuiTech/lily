package com.mei.hui.browser.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mei.hui.browser.entity.BlockHeaders;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 15:52
 **/
public interface BlockHeadersService extends IService<BlockHeaders> {
    public String selectBlockHeadersCount();
}
