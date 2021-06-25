package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.entity.FilDeadlines;
import com.mei.hui.miner.service.FilDeadlinesService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/25 16:01
 **/
@Api(tags = "fil矿工窗口记录")
@RestController
@RequestMapping("/fil/reportDeadlines")
public class FilDeadlinesController {

    @Autowired
    private FilDeadlinesService filDeadlinesService;


    @ApiOperation(value = "普通用户首页WindowPoSt的96个窗口")
    @GetMapping("/ninetySix")
    public Result selectFilDeadlinesNinetySixList(){
        return filDeadlinesService.selectFilDeadlinesNinetySixList();
    }


}
