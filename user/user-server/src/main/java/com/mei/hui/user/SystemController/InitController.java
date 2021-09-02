package com.mei.hui.user.SystemController;

import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.ShareLockThead;
import com.mei.hui.user.entity.SysRoleMenu;
import com.mei.hui.user.mapper.SysRoleMenuMapper;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/init")
@Api(tags = "初始化数据")
@Slf4j
public class InitController {
    public static int num = 0;

    private ExecutorService scheduledThreadPool = Executors.newFixedThreadPool(10);
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private RedisUtil redisUtil;
    @ApiOperation("分布式锁")
    @GetMapping("/shareLuck")
    public Result shareLuck(){
        log.info("开始时间:{}", LocalDateTime.now());
        for(int i=0;i<1000;i++){
            scheduledThreadPool.execute(new ShareLockThead(redisUtil,sysUserMapper));
        }
        return Result.OK;
    }

}
