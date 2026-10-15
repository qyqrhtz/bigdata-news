package com.example.bigdata_news.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bigdataNewsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bigdata News Analytics API")
                        .version("1.0.0")
                        .description("News analytics, dashboard statistics, semantic search and RAG chat APIs."));
    }
}
