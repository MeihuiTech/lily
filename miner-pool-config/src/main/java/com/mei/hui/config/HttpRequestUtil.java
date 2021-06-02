package com.mei.hui.config;

import com.mei.hui.util.SystemConstants;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import io.jsonwebtoken.Claims;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;

public class HttpRequestUtil {

    /**
     * 获取当前用户id
     * @return
     */
    public static Long getUserId(){
        Claims claims = parseToken();
        Integer userId = (Integer) claims.get(SystemConstants.USERID);
        return Long.valueOf(userId);
    }

    /**
     * 获取用户当前使用的币种id
     * @return
     */
    public static Long getCurrencyId(){
        Claims claims = parseToken();
        Object currency = claims.get(SystemConstants.CURRENCYID);
        if(currency != null){
            Integer currencyId = (Integer) currency;
            return Long.valueOf(currencyId);
        }
        return null;
    }

    /**
     * 解析token
     * @return
     */
    private static Claims parseToken(){
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String token = httpServletRequest.getHeader(SystemConstants.TOKEN);
        if(StringUtils.isEmpty(token)){
            throw new MyException(ErrorCode.MYB_111003.getCode(),"token 为空");
        }
        Claims claims = JwtUtil.parseToken(token);
        return claims;
    }
}
