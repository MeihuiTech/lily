package com.mei.hui.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.mei.hui.gateway.config.WhiteConfig;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
*@Description:
*@Author: 鲍红建
*@date: 2021/1/4
*/
@Component
@Slf4j
public class LoginFilter  implements GlobalFilter, Ordered {
    @Autowired
    private WhiteConfig gatewaySetting;
    @Autowired
    private UserFeignClient userFeignClient;


    /**
     * 执行过滤器中的业务逻辑
     *     对请求参数中的token进行判断
     *      如果存在此参数:代表已经认证成功
     *      如果不存在此参数 : 认证失败.
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        log.info("@========================start-{}========================","gateway");
        log.info("请求地址:{}",url);
        if(StringUtils.isNotEmpty(url) && url.contains("/profile/")){
            return chain.filter(exchange);
        }
        /**
         * 白名单不校验，并且不需要token校验
         */
        if(isWhiteUrl(url)){
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(SystemConstants.TOKEN);
        log.info("token = {}",token);
        //验签
        log.info("请求用户模块进行验签");
        Result auth = userFeignClient.authority(token,url);
        log.info("验签结果:{}", JSON.toJSONString(auth));
        String code = auth.getCode();
        if(ErrorCode.MYB_111002.getCode().equals(code) || ErrorCode.MYB_111003.getCode().equals(code)
                || ErrorCode.MYB_111004.getCode().equals(code)){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            Result result = new Result();
            result.setCode(code);
            result.setMsg(auth.getMsg());
            byte[] bytes = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }else{
            if(!ErrorCode.MYB_000000.getCode().equals(auth.getCode())){
                throw new MyException(auth.getCode(),auth.getMsg());
            }
        }
        log.info("@========================end-{}========================","gateway");
        return chain.filter(exchange);
    }

    /**
     * 指定过滤器优先级，值越小优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 200;
    }

    /**
     * 如果是白名单中的url，则返回true
     * @param url
     * @return
     */
    public boolean isWhiteUrl(String url){
        boolean flag = false;
        for (String regex : gatewaySetting.getWhiteUrls()){
            PathMatcher matcher = new AntPathMatcher();
            flag = matcher.match(regex, url);
            if(flag){
                log.info("匹配的白名单数据:{}",regex);
                break;
            }
        }
        return flag;
    }

}
