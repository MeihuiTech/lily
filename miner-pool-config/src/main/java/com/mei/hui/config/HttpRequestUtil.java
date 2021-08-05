package com.mei.hui.config;

import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.SystemConstants;
import io.jsonwebtoken.Claims;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class HttpRequestUtil {

    /**
     * 获取当前用户id
     * @return
     */
    public static Long getUserId(){
        Claims claims = parseToken();
        Object obj = claims.get(SystemConstants.USERID);
        if(obj == null){
            throw MyException.fail(ErrorCode.MYB_111004.getCode(),ErrorCode.MYB_111004.getMsg());
        }
        Integer userId = (Integer) obj;
        return Long.valueOf(userId);
    }

    /**
     * 获取当前用户的角色id
     * @return
     */
    public static Long getRoleId(){
        Claims claims = parseToken();
        Object obj = claims.get(SystemConstants.ROLEIDS);
        if(obj == null){
            throw MyException.fail(ErrorCode.MYB_111004.getCode(),ErrorCode.MYB_111004.getMsg());
        }
        List<Integer> roleIds = (List<Integer>) obj;
        return Long.valueOf(roleIds.get(0)+"");
    }

    /**
     * 获取用户当前使用的币种id
     * @return
     */
    public static Long getCurrencyId(){
        Claims claims = parseToken();
        Object currency = claims.get(SystemConstants.CURRENCYID);
        if(currency == null){
            throw MyException.fail(ErrorCode.MYB_111004.getCode(),ErrorCode.MYB_111004.getMsg());
        }
        Integer currencyId = (Integer) currency;
        return Long.valueOf(currencyId);
    }

    /**
     * 获取是否是游客
     * @return
     */
    public static boolean isVisitor(){
        Claims claims = parseToken();
        Object isVisitor = claims.get(SystemConstants.ISVISITOR);
        if(isVisitor == null){
            throw MyException.fail(ErrorCode.MYB_111004.getCode(),ErrorCode.MYB_111004.getMsg());
        }
        boolean isvisitor = (boolean) isVisitor;
        return isvisitor;
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
