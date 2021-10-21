package com.mei.hui.browser.controller;

import com.mei.hui.browser.service.BlockHeadersService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/10/14 16:27
 **/
@Api(tags = "区块头")
@RestController
@RequestMapping("/blockHeaders")
@Slf4j
public class BlockHeadersController {

    @Autowired
    private BlockHeadersService blockHeadersService;

    /**
     * 查询区块头表里的记录条数
     * @return
     */
    @GetMapping("/blockHeaders")
    public Result<String> selectBlockHeadersCount(){
        String count = blockHeadersService.selectBlockHeadersCount();
        return Result.success(count);
    }

}
