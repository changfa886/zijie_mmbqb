package com.xagent.dyin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="alioss")
@Data
public class AliOssConfig
{
    private String bucket;
    private String KeyID;
    private String KeySecret;
    private String innerEndPoint;
    private String foreignEndPoint;
    private String foreignHost;
//    private String surfixFwidth;
//    private String surfixSquare;
}
