package com.mei.hui.config;

import com.mei.hui.util.AESUtil;
import com.mei.hui.util.SystemConstants;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class HttpRequestUtil {

    public static Long getUserId(){
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String token = httpServletRequest.getHeader(SystemConstants.TOKEN);
        if(StringUtils.isEmpty(token)){
            throw new MyException(ErrorCode.MYB_111111.getCode(),"token 验证失败");
        }
        String userId = AESUtil.decrypt(token);
        return Long.valueOf(userId);
    }
}
