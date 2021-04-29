package com.mei.hui.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
*@Description: swagger 可以在多个微服务之间进行切换，动态从nacos获取服务，没有启动的服务不显示，
 *               只能在启动的微服务之间进行切换
*@Author: 鲍红建
*@date: 2021/1/9
*/
@Component
@Primary
@Slf4j
/*@Profile({"dev","test"})*/
public class SwaggerProvider implements SwaggerResourcesProvider {

    @Autowired
    private DiscoveryClient discoveryClient;

    public static final String API_URI = "/v2/api-docs";

    @Value("${spring.application.name}")
    private String projectName;

    @Override
    public List<SwaggerResource> get() {

        List<String> services = discoveryClient.getServices();
        //http://localhost:8061/shop-user//v2/api-docs
        List<SwaggerResource> resources = services.stream().filter(v->!projectName.equals(v)).map(v -> swaggerResource(v,"/" + v)).collect(Collectors.toList());
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location + API_URI);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }
}