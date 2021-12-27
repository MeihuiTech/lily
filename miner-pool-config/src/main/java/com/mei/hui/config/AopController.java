package com.mei.hui.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mei.hui.util.NotAop;
import com.mei.hui.util.PlatFormEnum;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 统一打印入参和出参日志格式
 * @author baohongjian
 */
@Component
@Slf4j
@Aspect
public class AopController {

	@Pointcut("execution(public * com.mei.hui.*.*Controller..*.*(..))")
	public void webLog(){}

    @Before("webLog()")
    public void before(JoinPoint joinPoint) {
    	log.info("@========================start========================");
		NotAop notAop = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NotAop.class);
		if(notAop == null){
			//获取请求的request
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			String token = request.getHeader(SystemConstants.TOKEN);
			Long userId = null;
			Long currencyId = null;
			if(StringUtils.isNotEmpty(token)){
				Claims claims = JwtUtil.parseToken(token);
				String platform = (String) claims.get(SystemConstants.PLATFORM);
				if(PlatFormEnum.web.name().equals(platform)){
					userId = HttpRequestUtil.getUserId();
					currencyId = HttpRequestUtil.getCurrencyId();
				}
			}
			log.info("@请求url:{},userId:{},currencyId:{},请求参数:{}",request.getRequestURL().toString(),userId,
					currencyId,getReqParameter(joinPoint));
		}
    }

    @AfterReturning(pointcut="webLog()",
            returning="returnValue")  
    public void afterReturning(JoinPoint point, Result returnValue){
		Result result = new Result();
    	if(returnValue != null){
    		result.setCode(returnValue.getCode());
    		result.setMsg(returnValue.getMsg());
		}
        log.info("@响应参数:{}",JSON.toJSONString(result));
        log.info("@========================end========================");
    }  

	public String getReqParameter(JoinPoint joinPoint) {
		// 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
		// 参数值
		Object[] args = joinPoint.getArgs();
		// 参数名
		String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
		JSONObject json = new JSONObject();
		for(int i = 0; i < argNames.length;i++){
			json.put(argNames[i],args[i]);
		}
		return json.toString();
	}

}