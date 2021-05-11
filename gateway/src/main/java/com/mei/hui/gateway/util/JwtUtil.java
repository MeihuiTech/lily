package com.mei.hui.gateway.util;

import com.auth0.jwt.exceptions.JWTDecodeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Slf4j
@Component
public class JwtUtil {


    @Value("${ruoyi.jwtSecret}")
    private String jwtSecret;

    private static String staticJwtSecret;

    @PostConstruct
    public void init(){
        staticJwtSecret = this.jwtSecret;
    }

    /**
     * 验签 并 解决 token
     * @param encodedToken
     * @return
     * @throws JWTDecodeException
     */
    public static Claims parseToken(String encodedToken) throws JWTDecodeException {
        Claims claims = Jwts.parser()
                .setSigningKey(staticJwtSecret)
                .parseClaimsJws(encodedToken)
                .getBody();
        return claims;
    }



}
