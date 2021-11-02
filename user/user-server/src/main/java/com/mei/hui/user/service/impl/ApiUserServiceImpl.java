package com.mei.hui.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.AESUtil;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.RSAUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.entity.ApiUser;
import com.mei.hui.user.mapper.ApiUserMapper;
import com.mei.hui.user.model.ApiTokenVO;
import com.mei.hui.user.model.GetTokenBO;
import com.mei.hui.user.service.ApiUserService;
import com.mei.hui.util.*;
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

    public Result<ApiTokenVO> getToken(@RequestBody GetTokenBO getTokenBO){
        long tokenExpires = getTokenBO.getTokenExpires();
        String accessKey = getTokenBO.getAccessKey();
        LambdaQueryWrapper<ApiUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApiUser::getAccessKey,accessKey);
        ApiUser apiUser = this.getOne(queryWrapper);
        if(apiUser == null){
            throw MyException.fail(ErrorCode.MYB_111111.getCode(),"accessKey 错误");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(SystemConstants.PLATFORM, PlatFormEnum.api.name());
        claims.put(SystemConstants.ACCESSKEY,accessKey);
        String token = Jwts.builder().setClaims(claims).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, staticRuoYiConfig.getJwtSecret()).compact();
        redisUtils.set(token,"",tokenExpires, TimeUnit.MINUTES);
        return Result.success(new ApiTokenVO().setToken(token));
    }





}
