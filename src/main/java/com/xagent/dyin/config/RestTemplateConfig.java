package com.xagent.dyin.config;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig
{
//    @Bean
//    public RestTemplate customRestTemplate() {
//        int timeoutMills = 6 * 1000;
//        int maxTotal = 50;
//        int defaultMaxPerRoute = 20;
//
//        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeoutMills)
//                .setConnectionRequestTimeout(timeoutMills).setSocketTimeout(timeoutMills).build();
//
//        HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(maxTotal)
//                .setMaxConnPerRoute(defaultMaxPerRoute).setDefaultRequestConfig(requestConfig).build();
//
//        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(
//                httpClient);
//
//        return new RestTemplate(httpRequestFactory);
//    }

    @Bean
    public RestTemplate httpRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        // 可以添加消息转换
        //restTemplate.setMessageConverters(...);
        // 可以增加拦截器
        //restTemplate.setInterceptors(...);
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(restTemplateConfigHttpClient());
    }

    @Bean
    public HttpClient restTemplateConfigHttpClient()
    {
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override
            public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                return 20 * 1000; // tomcat默认keepAliveTimeout为20s
            }
        };

//        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", SSLConnectionSocketFactory.getSocketFactory())
//                .build();
//        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(20, TimeUnit.SECONDS);
        // 设置最大连接数。高于这个值时，新连接请求，需要阻塞，排队等待
        connectionManager.setMaxTotal(200);
        connectionManager.closeIdleConnections(600, TimeUnit.SECONDS); // 关闭空闲连接池的机制
        // 路由是对maxTotal的细分
        // todo 后面调整从配置中心获取 maxPerRoute意思是某一个服务每次能并行接收的请求数量
        // 每个路由实际最大连接数默认值是由DefaultMaxPerRoute控制
        // MaxPerRoute设置的过小，无法支持大并发
        connectionManager.setDefaultMaxPerRoute(30);
        // validateAfterInactivity 空闲永久连接检查间隔，这个牵扯的还比较多
        // 在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s
        // 官方推荐使用这个来检查永久链接的可用性，而不推荐每次请求的时候才去检查
        connectionManager.setValidateAfterInactivity(6000);

        RequestConfig requestConfig = RequestConfig.custom()
                // 从连接池中获取连接的超时时间，超过该时间未拿到可用连接，
                .setConnectionRequestTimeout(2000)
                // 连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                .setConnectTimeout(2000)
                // 服务器返回数据(response)的时间，超过该时间抛出 read timeout
                .setSocketTimeout(14000)  // ssh的时间12秒太长了
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(connectionManager)
                .setRetryHandler(new DefaultHttpRequestRetryHandler())
                .build();
    }

}
