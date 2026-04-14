package com.example.bigdata_news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * 社交媒体热点新闻分析系统 - 启动入口
 * * 功能职责：
 * 1. 项目启动入口
 * 2. 开启 @EnableScheduling 以支持 AnalyticsScheduler 定时任务
 * 3. 注册 RestTemplate 以支持 NewsServiceImpl 调用 Python RAG 接口
 */
@SpringBootApplication
@EnableScheduling
public class BigdataNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigdataNewsApplication.class, args);
		System.out.println("====================================================");
		System.out.println("   BigData News Analysis System is running!         ");
		System.out.println("   Backend: http://localhost:8081                   ");
		System.out.println("   RAG Python Service: http://127.0.0.1:5000       ");
		System.out.println("====================================================");
	}

	/**
	 * 配置 RestTemplate 客户端
	 * 用于 Java 后端向 Python Flask 转发语义搜索请求
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}