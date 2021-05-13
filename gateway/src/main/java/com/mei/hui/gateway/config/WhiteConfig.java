package com.mei.hui.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties("setting")
@Component
@RefreshScope
public class WhiteConfig {

    private List<String> whiteUrls;

}