package com.mei.hui.config;

import com.mei.hui.util.SmsServiceNameEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* 公共方法类
*
* @description
* @author shangbin
* @date 2021/5/21 13:31
* @param
* @return
* @version v1.0.0
*/
@Slf4j
public class CommonUtil {


    public static HttpServletRequest getHttpServletRequest() {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取原请求头
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if(enumeration!=null){
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
    * 验证servicename是否在  验证码ServiceName枚举类  里,true存在，false不存在
    *
    * @description
    * @author shangbin
    * @date 2021/5/21 13:14
    * @param [serviceName]
    * @return boolean
    * @version v1.0.0
    */
    public static boolean isExistSmsServiceNameEnum(String serviceName) {
        for(SmsServiceNameEnum serviceNameEnum : SmsServiceNameEnum.values()) {
            if (serviceNameEnum.name().equals(serviceName)) {
                return true;
            }
        }
        return  false;
    }

}
