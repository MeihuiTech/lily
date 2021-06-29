package com.mei.hui.config;

import com.alibaba.fastjson.serializer.BigDecimalCodec;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.context.annotation.Configuration;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class MyWebmvcConfiguration{

    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverter() {

        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastJsonConfig.setSerializerFeatures(
                // 保留 Map 空的字段
                SerializerFeature.WriteMapNullValue,
                // 将 String 类型的 null 转成""
                SerializerFeature.WriteNullStringAsEmpty,
                // 将 Number 类型的 null 转成 0
                SerializerFeature.WriteNullNumberAsZero,
                // 将 List 类型的 null 转成 []
                SerializerFeature.WriteNullListAsEmpty,
                // 将 Boolean 类型的 null 转成 false
                SerializerFeature.WriteNullBooleanAsFalse,
                // 避免循环引用
                SerializerFeature.DisableCircularReferenceDetect
        );
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastJsonHttpMessageConverter);
    }
}