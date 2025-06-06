package com.pfa.api.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig {

    @Bean(name = "asyncExecutor")
    Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); 
        executor.setMaxPoolSize(4); 
        executor.setQueueCapacity(20); 
        executor.setKeepAliveSeconds(180); 
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
}
