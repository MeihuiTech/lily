package com.mei.hui.miner.SystemController;

import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "旷工模块【鲍红建】")
@RestController
@RequestMapping("/miner")
public class MinerController {

    @Value("${spring.application.name}")
    private String projectName;

    @Autowired
    private UserFeignClient userFeignClient;

    @ApiOperation("获取旷工信息")
    @GetMapping("/getMiner")
    public Result getMiner(){
        Result user_server = userFeignClient.getSysUser();
        return Result.success(projectName +"----" + user_server.getData());
    }
}
