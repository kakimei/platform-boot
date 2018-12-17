package com.platform;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
@EnableScheduling
@ServletComponentScan(basePackages = {"com.platform"})
public class Application implements AsyncConfigurer {

	public static void main(String[] args){
		SpringApplication springApplication = new SpringApplication(Application.class);
		springApplication.run(args);
	}

	@Value("#{environment['async.call.thread.pool.corePoolSize']}")
	protected int ASYNC_CORE_POOL_SIZE;
	@Value("#{environment['async.call.thread.pool.maxPoolSize']}")
	protected int ASYNC_MAX_POOL_SIZE;
	@Value("#{environment['async.call.thread.pool.queueCapacity']}")
	protected int ASYNC_QUEUE_CAPACITY;

	@Autowired
	private TaskExecutor threadPoolTaskExecutor;

	@Override
	public Executor getAsyncExecutor() {
		return threadPoolTaskExecutor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}

	@Bean(name = "threadPoolTaskExecutor")
	public TaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor threadPoolTaskExecutor =
			new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(ASYNC_CORE_POOL_SIZE);
		threadPoolTaskExecutor.setMaxPoolSize(ASYNC_MAX_POOL_SIZE);
		threadPoolTaskExecutor.setQueueCapacity(ASYNC_QUEUE_CAPACITY);
		threadPoolTaskExecutor.setThreadNamePrefix("SubscribeOrderWorker");
		threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());

		return threadPoolTaskExecutor;
	}
}
