package com.mei.hui.config.smsConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsConfig {

    private String url;

    private String username;

    private String password;

    private String token;

    private String templateId;

}
