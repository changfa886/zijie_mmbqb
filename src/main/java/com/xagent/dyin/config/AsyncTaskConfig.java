package com.xagent.dyin.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by jonty on 2019/8/11.
 * @ComponentScan("") 标明会在哪个包下使用多线程
 *
 * 当一个任务通过execute(Runnable)方法欲添加到线程池时：
  如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
  如果此时线程池中的数量等于 corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
  如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
  如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 handler所指定的策略来处理此任务。也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程 maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
  当线程池中的线程数量大于 corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。
 *
 */

@Configuration
@ComponentScan("com.xagent.dyin.thread")
@EnableAsync
public class AsyncTaskConfig implements AsyncConfigurer
{
    @Override
    public Executor getAsyncExecutor()
    {
        // 配置类实现AsyncConfigurer接口并重写 getAsyncExecutor 方法,并返回一个 ThreadPoolTaskExecutor,这样我们就获得了一个线程池 taskExecutor
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);  // 最小线程数 线程池维护线程的最少数量  注意启动时boss端启动了很多线程，这个数量要满足
        taskExecutor.setMaxPoolSize(100);  // 最大线程数 线程池维护线程的最大数量
        taskExecutor.setQueueCapacity(32);  // 等待队列 线程池所使用的缓冲队列
        taskExecutor.setKeepAliveSeconds(60);  // 线程池维护线程所允许的空闲时间：超过core size的那些线程，任务完成后，再经过这个时长（秒）会被结束掉

        taskExecutor.initialize();

        return taskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
