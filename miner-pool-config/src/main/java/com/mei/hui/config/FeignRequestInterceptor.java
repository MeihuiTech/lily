package com.mei.hui.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
*@Description:feign拦截器，微服务间传递token
*@Author: 鲍红建
*@date: 2021/1/14
*/
@Configuration
@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        HttpServletRequest httpServletRequest =   CommonUtil.getHttpServletRequest();
        if(httpServletRequest!=null){
            Map<String, String> headers = CommonUtil.getHeaders(httpServletRequest);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                template.header(entry.getKey(), entry.getValue());
            }
            log.debug("请求header:{}", template.toString());
        }
    }

}