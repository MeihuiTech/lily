package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.TicketPageListBO;
import com.mei.hui.miner.feign.vo.TicketPageListVO;
import com.mei.hui.miner.service.ISwarmTicketService;
import com.mei.hui.util.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:12
 **/
@Api(value = "节点出票信息",tags = "swarm出票信息")
@Slf4j
@RestController
@RequestMapping("/swarmTicket")
public class SwarmTicketController {
    @Autowired
    private ISwarmTicketService swarmTicketService;

    @ApiOperation("swarm出票分页列表【鲍红建】")
    @PostMapping("/ticketPageList")
    public PageResult<TicketPageListVO> ticketPageList(@RequestBody TicketPageListBO bo){
        return swarmTicketService.ticketPageList(bo);
    }
}
