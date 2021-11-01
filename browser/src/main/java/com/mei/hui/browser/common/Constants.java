package com.mei.hui.browser.common;

import com.mei.hui.util.SystemConstants;

/**
 * 常量类
 */
public interface Constants extends SystemConstants {

    //es算力索引，历史明细
    String ES_POWER_INDEX = "power";

    //算力表，存矿工的最新数据，每个矿工存一条数据
    String ES_POWER_LATEST_INDEX = "power_latest";

    //出块明细
    String ES_BLOCK_INDEX = "block";

    // 首页全站搜索
    String ES_FULL_TEXT_INDEX = "query_v1";


}
