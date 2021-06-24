package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.GeneralViewVo;
import com.mei.hui.miner.service.FilBaselinePowerDayAggService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "免登陆首页")
@RestController
@RequestMapping("/noAuthority")
public class GeneralController {

    @Autowired
    private FilBaselinePowerDayAggService baselinePowerDayAggService;
    /**
     * 新增矿工信息
     */
    @ApiOperation(value = "免登陆首页，概览【鲍红建】")
    @PostMapping("/generalView")
    public Result<GeneralViewVo> generalView(){
        return baselinePowerDayAggService.generalView();
    }
}
