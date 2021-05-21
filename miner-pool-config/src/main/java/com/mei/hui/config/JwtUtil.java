package com.mei.hui.config;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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

    private static RuoYiConfig staticRuoYiConfig;

    @Autowired
    public void setRuoYiConfig(RuoYiConfig ruoYiConfig) {
        this.staticRuoYiConfig = ruoYiConfig;
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
                .setIssuedAt(new Date())
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
    public static Claims parseToken(String encodedToken)  {
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    .setSigningKey(staticRuoYiConfig.getJwtSecret())
                    .parseClaimsJws(encodedToken)
                    .getBody();
        }catch (ExpiredJwtException exp){
            throw MyException.fail(ErrorCode.MYB_111002.getCode(),ErrorCode.MYB_111002.getMsg());
        }catch (Exception dex){
            throw MyException.fail(ErrorCode.MYB_111004.getCode(),"token 验签错误");
        }
        return claims;
    }



}
