package com.mei.hui.config;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.mei.hui.config.config.RuoYiConfig;
import com.mei.hui.util.MyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class JwtUtil {

    @Autowired
    private RuoYiConfig ruoYiConfig;
    private static RuoYiConfig staticRuoYiConfig;

    @PostConstruct
    public void init(){


        if(ruoYiConfig.getJwtMinutes() == 0){
            //默认
            ruoYiConfig.setJwtMinutes(1);
        }
        if(StringUtils.isEmpty(ruoYiConfig.getJwtSecret())){
            log.error("ruoyi.jwtSecret不能为空");
            throw new RuntimeException("ruoyi.jwtSecret不能为空");
        }
        staticRuoYiConfig = this.ruoYiConfig;
    }
    /**
     * 加密
     * @param claims
     * @return
     * @throws JWTCreationException
     */
    public static String createToken(Map<String, Object> claims) throws JWTCreationException {
        Date expDate = new Date(System.currentTimeMillis() + staticRuoYiConfig.getJwtMinutes() * 60 * 1000);
        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expDate)
                .signWith(SignatureAlgorithm.HS256, staticRuoYiConfig.getJwtSecret()).compact();
        return token;
    }

    /**
     * 验签 并 解决 token
     * @param encodedToken
     * @return
     * @throws JWTDecodeException
     */
    public static Claims parseToken(String encodedToken) throws JWTDecodeException {
        Claims claims = Jwts.parser()
                .setSigningKey(staticRuoYiConfig.getJwtSecret())
                .parseClaimsJws(encodedToken)
                .getBody();
        return claims;
    }



}
