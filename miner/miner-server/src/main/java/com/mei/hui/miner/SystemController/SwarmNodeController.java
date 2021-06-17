package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.feign.vo.FindNodeListVO;
import com.mei.hui.miner.feign.vo.NodePageListBO;
import com.mei.hui.miner.feign.vo.NodePageListVO;
import com.mei.hui.miner.service.ISwarmNodeService;
import com.mei.hui.util.PageResult;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author shangbin
 * @version v1.0.0
 * @date 2021/6/16 14:12
 **/
@Api(value = "swarm节点信息",tags = "swarm节点信息")
@Slf4j
@RestController
@RequestMapping("/swarmNode")
public class SwarmNodeController {

    @Autowired
    private ISwarmNodeService swarmNodeService;

    @ApiOperation("节点分页列表【鲍红建】")
    @PostMapping("/nodePageList")
    public PageResult<NodePageListVO> nodePageList(@RequestBody NodePageListBO bo){
        return swarmNodeService.nodePageList(bo);
    }

    @ApiOperation("获取节点ip列表【鲍红建】")
    @PostMapping("/findNodeList")
    public Result<List<FindNodeListVO>> findNodeList(){
        return swarmNodeService.findNodeList();
    }
}
