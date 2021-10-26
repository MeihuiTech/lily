package com.mei.hui.browser.common.es;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class EsConfig {

    private List<EsNode> nodes = new ArrayList<>();
    private String userName;
    private String password;
    private int connectionRequestTimeout;
    private int connectTimeout;
    private int socketTimeout;
}
