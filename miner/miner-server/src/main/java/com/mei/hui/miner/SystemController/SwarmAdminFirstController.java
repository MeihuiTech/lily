package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.SwarmAdminFirstCollectVO;
import com.mei.hui.miner.service.ISwarmAdminFirstService;
import com.mei.hui.util.BasePage;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@Api(tags = "Swarm管理员-首页")
@RestController
@RequestMapping("/swarm/admin/first")
public class SwarmAdminFirstController {
    @Autowired
    private ISwarmAdminFirstService swarmAdminFirstService;

    @ApiOperation(value = "管理员首页-平台概览",notes = "管理员首页-平台概览出参：\n" +
            "\n" +
            "ticketValid总有效出票数\n" +
            "todayTicketValid今日有效出票数\n" +
            "nodeValid有效节点\n" +
            "linkNum平台总连接数")
    @GetMapping("/allCount")
    public Result adminFirstAllCount(){
        SwarmAdminFirstCollectVO swarmAdminFirstCollectVO = swarmAdminFirstService.swarmAdminFirstAllCount();
        return Result.success(swarmAdminFirstCollectVO);
    }

    @ApiOperation(value = "管理员首页-平台有效出票数排行榜",notes = "管理员首页-平台有效出票数排行榜出参：\n" +
            "\n" +
            "userName用户姓名\n" +
            "ticketValid总有效出票数\n" +
            "ticketValidPercent总有效出票数所占百分比\n" +
            "todayTicketValid今日有效出票数\n" +
            "nodeValid有效节点\n" +
            "linkNum平台总连接数")
    @GetMapping("/ticketValidPage")
     public Map<String,Object> ticketValidPage(BasePage basePage){
        return swarmAdminFirstService.ticketValidPage(basePage);
    }




}
