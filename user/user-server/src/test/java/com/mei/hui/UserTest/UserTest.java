package com.mei.hui.UserTest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.config.smsConfig.SmsConfig;
import com.mei.hui.user.UserApplication;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.entity.SysMenu;
import com.mei.hui.user.entity.SysRoleMenu;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysRoleMenuMapper;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.util.IdUtils;
import com.mei.hui.util.SystemConstants;
import com.mei.hui.util.UUID;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserApplication.class)
@Slf4j
public class UserTest {


    @Test
    public void testToken() {
        String accessKey = IdUtils.fastSimpleUUID();
        String secretKey = IdUtils.fastSimpleUUID();
        log.info("accessKey = {},secretKey = {}",accessKey,secretKey);


    }



}
