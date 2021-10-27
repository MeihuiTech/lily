package com.mei.hui.browser.controller;

import com.mei.hui.browser.model.PowerRankingVO;
import com.mei.hui.browser.service.PowerService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Api(tags = "算力排行【排行榜】")
@RestController
@RequestMapping("/power")
public class PowerController {

    @Autowired
    private PowerService powerService;

    @ApiOperation(value = "获取系统划转记录详细信息")
    @PostMapping("/transferRecordDetail")
    public PageResult<PowerRankingVO> powerRanking(@RequestBody BasePage page) throws IOException {
        return powerService.powerRanking(page);
    }


}
