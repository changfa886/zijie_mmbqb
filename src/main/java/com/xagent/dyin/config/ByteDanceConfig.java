package com.xagent.dyin.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
public class ByteDanceConfig
{
    @Value("${bytedance.app.appid}")
    private String appAppid;
    @Value("${bytedance.app.appsecret}")
    private String appAppsecret;

    @Value("${bytedance.pay.appid}")
    private String payAppid;
    @Value("${bytedance.pay.appsecret}")
    private String payAppsecret;
    @Value("${bytedance.pay.merchantid}")
    private String payMerchantid;
}
