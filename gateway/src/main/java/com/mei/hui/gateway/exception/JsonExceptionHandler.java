package com.mei.hui.gateway.exception;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.http.HttpStatus;
/**
 * 自定义异常处理
 * <p>异常时用JSON代替HTML异常信息<p>
 * @author baohongjian
 */
@Slf4j
public class JsonExceptionHandler extends DefaultErrorWebExceptionHandler {

    public JsonExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }
    /**
     * 获取异常属性
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable e = super.getError(request);
        Result rs = new Result(ErrorCode.MYB_111111.getCode(), ErrorCode.MYB_111111.getMsg());
        if (e instanceof MyException) {
            MyException myException = (MyException) e;
            rs = new Result(myException.getCode(), myException.getMsg());
        }
        log.error("全局异常统一处理:", e);
        log.info("@响应参数:{}", JSON.toJSONString(rs));
        log.error("@========================end-{}========================","gateway");
        Map<String,Object> map = new HashMap<>();
        map.put("code",rs.getCode());
        map.put("msg",rs.getMsg());
        return map;
    }

    /**
     * 指定响应处理方法为JSON处理的方法
     * @param errorAttributes
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        return 200;
    }

}
