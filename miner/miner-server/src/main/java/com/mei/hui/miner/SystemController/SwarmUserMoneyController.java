package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.SwarmUserMoneyBO;
import com.mei.hui.miner.feign.vo.SwarmUserMoneyVO;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.util.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员-用户收益-用户列表
 *
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/17 11:59
 **/
@Slf4j
@RestController
@RequestMapping("/swarm/admin/userMoney")
@Api(value = "swarm管理员-用户收益-用户列表",tags = "管理员-用户收益-用户列表")
public class SwarmUserMoneyController {

    @Autowired
    private ISwarmNodeService swarmNodeService;

    @ApiOperation(value = "管理员-用户收益-多条件分页查询用户列表",notes = "管理员-用户收益-用户列表出参：\n" +
            "userId用户Id\n" +
            "userName用户名\n" +
            "ticketValid累计有效出票数\n" +
            "money出票资产")
    @GetMapping("/selectUserMoneyList")
    public PageResult<SwarmUserMoneyVO> selectUserMoneyList(SwarmUserMoneyBO swarmUserMoneyBO){
        return swarmNodeService.selectUserMoneyList(swarmUserMoneyBO);
    }

}
