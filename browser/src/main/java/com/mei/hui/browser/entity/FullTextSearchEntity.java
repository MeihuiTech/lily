package com.mei.hui.browser.entity;

import lombok.Data;

/**
 * 首页全站搜索
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/29 16:16
 **/
@Data
public class FullTextSearchEntity {


    private String query_string;

    private String type;

}
