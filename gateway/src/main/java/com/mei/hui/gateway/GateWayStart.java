package com.mei.hui.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
*@Description:
*@Author: 鲍红建
*@date: 2020/12/31
*/
@SpringBootApplication(scanBasePackages = {"com.mei.hui.*"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.mei.hui.*"})
public class GateWayStart {

    public static void main(String[] args) {
        SpringApplication.run(GateWayStart.class, args);
    }
}
