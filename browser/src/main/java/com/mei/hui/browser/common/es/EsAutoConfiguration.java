package com.mei.hui.browser.common.es;

import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class EsAutoConfiguration {
    @Autowired
    private EsConfig esConfig;
    @Bean
    public RestHighLevelClient client(){
        List<EsNode> nodes = esConfig.getNodes();
        if(nodes.size() == 0){
            log.error("您还没有配置elasticsearch");
            return null;
        }
        HttpHost[] httpHosts = new HttpHost[nodes.size()];
        for(int i=0;i<nodes.size();i++) {
            EsNode node= nodes.get(i);
            httpHosts[i] = new HttpHost(node.getIp(), node.getPort(), node.getSchema());
        }

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(esConfig.getUserName(), esConfig.getPassword()));

        SSLFactory sslFactory = SSLFactory.builder()
                .withUnsafeTrustMaterial()
                .withHostnameVerifier((host, session) -> true).build();

        RestClientBuilder builder = RestClient.builder(httpHosts).setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(esConfig.getConnectTimeout());
            requestConfigBuilder.setSocketTimeout(esConfig.getSocketTimeout());
            requestConfigBuilder.setConnectionRequestTimeout(esConfig.getConnectionRequestTimeout());
            return requestConfigBuilder;
        }).setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.disableAuthCaching();
            httpClientBuilder.setSSLContext(sslFactory.getSslContext());
            httpClientBuilder.setSSLHostnameVerifier(sslFactory.getHostnameVerifier());
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        });

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

}
