package com.mei.hui.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.RSAUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.entity.ApiUser;
import com.mei.hui.user.mapper.ApiUserMapper;
import com.mei.hui.user.service.ApiUserService;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 对外api调用客户信息配置 服务实现类
 * @author 鲍红建
 * @since 2021-07-14
 */
@Service
public class ApiUserServiceImpl extends ServiceImpl<ApiUserMapper, ApiUser> implements ApiUserService {

    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private RuoYiConfig staticRuoYiConfig;

    public Result getToken(String body){
        String str = RSAUtil.decrypt(body);
        JSONObject json = JSON.parseObject(str);
        String accessKey = json.getString("accessKey");
        long tokenExpires = json.getLongValue("tokenExpires");

        Map<String, Object> claims = new HashMap<>();
        claims.put(SystemConstants.PLATFORM,Constants.API);
        claims.put(SystemConstants.ACCESSKEY,accessKey);
        String token = Jwts.builder().setClaims(claims).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, staticRuoYiConfig.getJwtSecret()).compact();
        redisUtils.set(token,"",tokenExpires, TimeUnit.MINUTES);
        return Result.success(token);
    }





}
