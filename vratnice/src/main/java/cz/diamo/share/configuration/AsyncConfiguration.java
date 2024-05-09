package cz.diamo.share.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@EnableAsync

@Configuration

public class AsyncConfiguration {

  @Bean("threadPoolTaskExecutor")
  public TaskExecutor getAsyncExecutor() {

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(25);
    // executor.setMaxPoolSize(20);
    // executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setThreadNamePrefix("Async-");
    executor.initialize(); // this is important, otherwise an error is thrown
    return new DelegatingSecurityContextAsyncTaskExecutor(executor); // use this special TaskExecuter
  }
}