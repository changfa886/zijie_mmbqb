package com.xagent.dyin.config;

import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.lang.reflect.Method;
import java.util.concurrent.Executors;

/**
 * 如果定时任务非常多,或者有的任务很耗时,会影响到其他定时任务的执行,
 * 因为schedule 默认是单线程的,一个任务在执行时,其他任务是不能执行的.
 * 解决办法是重新配置schedule,改为多线程执行
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer
{
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {
        Method[] methods = BatchProperties.Job.class.getMethods();
        int defaultPoolSize = 3;
        int corePoolSize = 0;
        if (methods != null && methods.length > 0)
        {
            for (Method method : methods)
            {
                Scheduled annotation = method.getAnnotation(Scheduled.class);
                if (annotation != null)
                {
                    corePoolSize++;
                }
            }
            if (defaultPoolSize > corePoolSize)
            {
                corePoolSize = defaultPoolSize;
            }
        }
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(corePoolSize));
    }
}
