package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.SwarmHomePageVO;
import com.mei.hui.miner.service.ISwarmAggService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:12
 **/
@Api(tags = "swarm聚合统计")
@Slf4j
@RestController
@RequestMapping("/swarmAgg")
public class SwarmAggController {

    @Autowired
    private ISwarmAggService swarmAggService;

    @ApiOperation("swarm 首页")
    @PostMapping("/homePage")
    public Result<SwarmHomePageVO> homePage(){
        return swarmAggService.homePage();
    }
}
