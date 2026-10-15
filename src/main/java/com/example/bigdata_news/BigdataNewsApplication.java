package com.example.bigdata_news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * 新闻大数据分析与 RAG 语义检索平台启动入口。
 *
 * 功能职责：
 * 1. 启动 Spring Boot 后端服务。
 * 2. 开启 @EnableScheduling 以支持 AnalyticsScheduler 定时任务。
 * 3. 注册 RestTemplate 以支持后端调用 Python RAG 服务。
 */
@SpringBootApplication
@EnableScheduling
public class BigdataNewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigdataNewsApplication.class, args);
		System.out.println("====================================================");
		System.out.println("   Bigdata News Analytics Platform is running.      ");
		System.out.println("   Backend: http://localhost:8081                   ");
		System.out.println("   Health:  http://localhost:8081/api/health        ");
		System.out.println("   Swagger: http://localhost:8081/swagger-ui.html   ");
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
