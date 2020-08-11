package com.xagent.dyin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class MyApplicationRunner implements ApplicationRunner
{
    private static Logger logger = LoggerFactory.getLogger(MyApplicationRunner.class);

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception
    {
        logger.info("profile === " + profile);

    }
}
