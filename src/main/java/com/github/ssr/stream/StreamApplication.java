package com.github.ssr.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class StreamApplication extends SpringBootServletInitializer  {
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(StreamApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(StreamApplication.class, args);
	}
	
	@Configuration
	public static class WebConfig extends WebMvcConfigurerAdapter {

		@Override
		public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
			configurer.setDefaultTimeout(-1);
			configurer.setTaskExecutor(asyncTaskExecutor());
		}
		
		@Bean
		public AsyncTaskExecutor asyncTaskExecutor() {
			return new SimpleAsyncTaskExecutor("async");
		}
		
	}
}
