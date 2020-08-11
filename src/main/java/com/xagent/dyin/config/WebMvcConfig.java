package com.xagent.dyin.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@EnableWebMvc
@Configuration
public class WebMvcConfig implements WebMvcConfigurer // implements WebMvcConfigurer 或 extends WebMvcConfigurationSupport | WebMvcConfigurerAdapter
{
    private static Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);

    @Autowired
    private CorsDomainIntercept corsDomainIntercept;
    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        /**
         * 序列换成json时,将所有的long变成string
         * 因为js中得数字类型不能包含所有的java long值
         */
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);

        converters.add(jackson2HttpMessageConverter);


        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        List<MediaType> supportMediaTypeList = new ArrayList<>();
//        supportMediaTypeList.add(MediaType.APPLICATION_FORM_URLENCODED);
//        supportMediaTypeList.add(MediaType.APPLICATION_JSON_UTF8);
//        supportMediaTypeList.add(MediaType.APPLICATION_JSON);
        supportMediaTypeList.add(MediaType.TEXT_XML);  // 为了接受微信三方授权的回调，xml格式
        stringHttpMessageConverter.setSupportedMediaTypes(supportMediaTypeList);
        converters.add(stringHttpMessageConverter);
    }

      // 跨域
//    @Override
//    public void addCorsMappings(CorsRegistry registry)
//    {
//        registry.addMapping("/**")
//                .allowedOrigins("*")     // 配置允许的源
//                .allowCredentials(true)  // 配置是否允许发送Cookie, 用于 凭证请求
//                .allowedMethods("GET", "POST", "OPTIONS", "PUT")
//                //.allowedHeaders("*")  // 配置允许的自定义请求头, 用于 预检请求
//                //.exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
//                .maxAge(3600);
//    }

    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(corsDomainIntercept).addPathPatterns("/**");

        registry.addInterceptor(adminInterceptor)
                //排除拦截
                .excludePathPatterns("/admin/account/login/**")
                //拦截路径
                .addPathPatterns("/admin/**");
    }

    // 请求拦截器
//    @Override
//    public void addInterceptors(InterceptorRegistry registry)
//    {
//        registry.addInterceptor(new SecurityInterceptor())
//                //排除拦截
//                .excludePathPatterns("/user/login")
//                .excludePathPatterns("/user/logout")
//                //拦截路径
//                .addPathPatterns("/**");
//                //.addPathPatterns("/user/**");
//    }


}
