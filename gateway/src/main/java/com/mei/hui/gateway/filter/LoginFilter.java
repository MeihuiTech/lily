package com.mei.hui.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.mei.hui.gateway.config.WhiteConfig;
import com.mei.hui.user.feign.feignClient.UserFeignClient;
import com.mei.hui.user.feign.vo.SignBO;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import com.mei.hui.util.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
        /**
         * 白名单不校验
         */
        if(gatewaySetting.getWhiteUrls().contains(url)){
            return chain.filter(exchange);
        }
        String token = exchange.getRequest().getHeaders().getFirst(SystemConstants.TOKEN);
        log.info("token = {}",token);
        //验签
        log.info("请求用户模块进行验签");
        SignBO signBO = new SignBO();
        signBO.setToken(token);
        Result signin = userFeignClient.sign(signBO);
        log.info("验签结果:{}", JSON.toJSONString(signin));
        if(!ErrorCode.MYB_000000.getCode().equals(signin.getCode())){
            throw new MyException(signin.getCode(),signin.getMsg());
        }
      /*  //向headers中放文件，记得build
        ServerHttpRequest host = exchange.getRequest().mutate().header(SystemConstants.TOKEN, token).build();
        ServerWebExchange build = exchange.mutate().request(host).build();*/
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
}
