package com.mei.hui.browser.Controller;

import com.mei.hui.browser.service.FullTextSearchService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
* 首页全站搜索
*
* @description
* @author shangbin
* @date 2021/10/29 13:59
* @param
* @return
* @version v1.4.1
*/
@Api(tags = "首页全站搜索")
@RestController
@RequestMapping("/fullTextSearch")
@Slf4j
public class FullTextSearchController {

    @Autowired
    private FullTextSearchService fullTextSearchService;

    /**
     * 首页全站搜索
     * @return
     */
    @GetMapping("/fullTextSearch")
    public Result<String> selectFullTextSearch(String searchText) throws IOException {
        String type = fullTextSearchService.selectFullTextSearch(searchText);
        return Result.success(type);
    }

}
