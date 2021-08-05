package com.mei.hui.config;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.model.TokenBO;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.SystemConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
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
     * @param tokenBO
     * @return
     * @throws JWTCreationException
     */
    public static String createToken(TokenBO tokenBO) throws JWTCreationException {
        if(tokenBO.getCurrencyId() == null){
            throw MyException.fail(ErrorCode.MYB_111111.getCode(),"币种id 不能为空");
        }
        if(StringUtils.isEmpty(tokenBO.getPlatform())){
            throw MyException.fail(ErrorCode.MYB_111111.getCode(),"platform 登陆平台参数不能为空");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(SystemConstants.USERID,tokenBO.getUserId());
        claims.put(SystemConstants.CURRENCYID,tokenBO.getCurrencyId());
        claims.put(SystemConstants.PLATFORM,tokenBO.getPlatform());
        claims.put(SystemConstants.ISVISITOR,tokenBO.isVisitor());
        claims.put(SystemConstants.ROLEIDS,tokenBO.getRoleIds());

        String token = Jwts.builder().setClaims(claims).setIssuedAt(new Date())
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
