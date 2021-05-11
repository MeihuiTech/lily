package com.mei.hui.config;

import com.mei.hui.util.AESUtil;
import com.mei.hui.util.SystemConstants;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import io.jsonwebtoken.Claims;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestUtil {

    public static Long getUserId(){
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String token = httpServletRequest.getHeader(SystemConstants.TOKEN);
        if(StringUtils.isEmpty(token)){
            throw new MyException(ErrorCode.MYB_111111.getCode(),"token 验证失败");
        }
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = (Integer) claims.get(SystemConstants.USERID);
        return Long.valueOf(userId);
    }
}
