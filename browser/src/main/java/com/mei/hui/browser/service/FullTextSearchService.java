package com.mei.hui.browser.service;

import java.io.IOException;
import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/29 14:34
 **/
public interface FullTextSearchService {

    /**
     * 首页全站搜索
     * @return
     */
    public List<String> selectFullTextSearch(String searchText) throws IOException;
}
