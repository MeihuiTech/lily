package com.mei.hui.browser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
*@Description:
*@Author: 鲍红建
*@date: 2020/12/29
*/
@SpringBootApplication(scanBasePackages = {"com.mei.hui.*"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.mei.hui.*"})
@MapperScan("com.mei.hui.miner.mapper")
public class BrowserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BrowserApplication.class, args);
    }
}
