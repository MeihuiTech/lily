package com.mei.hui.user.common;

import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.SystemController.InitController;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class ShareLockThead implements Runnable {
    private SysUserMapper sysUserMapper;
    private RedisUtil redisUtil;
    public ShareLockThead(RedisUtil redisUtil,SysUserMapper sysUserMapper){
        this.redisUtil = redisUtil;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public void run() {
        String key = "service_num";
       boolean flag = redisUtil.lock(key);
        if(!flag){
            log.info("获取锁失败");
            return;
        }
        int m = InitController.num;
        int n = m+1;
        InitController.num = n;

        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setCreateTime(LocalDateTime.now());
        sysUser.setRemark(InitController.num+"");
        sysUserMapper.updateById(sysUser);
        redisUtil.unlock(key);
    }
}
