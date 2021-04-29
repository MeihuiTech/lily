package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.entity.PoolInfo;
import com.mei.hui.miner.service.IPoolInfoService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息
 * @author tianzk
 */
@Api(tags = "矿池信息")
@RestController
@RequestMapping("/system")
@Slf4j
public class PoolInfoController{

    @Autowired
    private IPoolInfoService poolInfoService;


    @ApiOperation(value = "用户矿池信息")
    @GetMapping("/pool")
    public Result getInfo() {
        Long userId = HttpRequestUtil.getUserId();
        PoolInfo rr = poolInfoService.selectPoolInfoByUserId(userId);
        Result result = new Result(ErrorCode.MYB_000000.getCode(),ErrorCode.MYB_000000.getMsg());
        result.setData(rr);
        return result;
    }
}
