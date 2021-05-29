package com.mei.hui.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mei.hui.util.NotAop;
import lombok.extern.slf4j.Slf4j;
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
 * 
 *
 * Description: 统一打印入参和出参日志格式
 *
 * @author baohongjian
 * @version 1.0
 * <pre>
 * Modification History: 
 * Date         Author      Version     Description 
 * ------------------------------------------------------------------ 
 * 2017年10月10日    bao       1.0        1.0 Version 
 * </pre>
 */
@Component
@Slf4j
@Aspect
public class AopController {

	@Value("${spring.application.name}")
	private String  projectName;

	@Pointcut("execution(public * com.mei.hui.*.*Controller..*.*(..))")
	public void webLog(){}

    @Before("webLog()")
    public void before(JoinPoint joinPoint) {
    	log.info("@========================start-{}========================",projectName);
		NotAop notAop = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(NotAop.class);
		if(notAop == null){
			//获取请求的request
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			log.info("@请求url:{},userId:{},currencyId:{},请求参数:{}",request.getRequestURL().toString(),HttpRequestUtil.getUserId(),
					HttpRequestUtil.getCurrencyId(),getReqParameter(joinPoint));
		}

    }
    
/*    @AfterThrowing(pointcut = "webLog()", throwing = "ex")
    public void afterThrowing(Exception ex) {  
    	 log.error("@出现异常信息,如下:", ex);
    	 log.info("@========================end-{}========================",projectName);
    }
    */
    @AfterReturning(pointcut="webLog()",
            returning="returnValue")  
    public void afterReturning(JoinPoint point, Object returnValue){
        log.info("@响应参数:{}",JSON.toJSONString(returnValue));
        log.info("@========================end-{}========================",projectName);
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