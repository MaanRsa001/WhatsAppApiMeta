package com.maan.whatsapp;

import java.security.Security;
import java.util.concurrent.Executor;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;

@SpringBootApplication
//@EnableSwagger2
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@OpenAPIDefinition(
	    info = @Info(title = "Whatsapp chat bot restful services", version = "v1"),
	    security = {@SecurityRequirement(name = "bearerAuth")}
	)
	@SecurityScheme(
	    name = "bearerAuth",
	    type = SecuritySchemeType.HTTP,
	    scheme = "bearer",
	    bearerFormat = "JWT"
	)
public class WhatsAppApiApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WhatsAppApiApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(WhatsAppApiApplication.class, args);
	}
	
	 @PostConstruct
	    public void init() {
	        // Add Bouncy Castle as a security provider
	       // Security.addProvider(new BouncyCastleProvider());
	    }
	@Bean
	public Executor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("WhatsAppExecutor_");
		executor.initialize();

		return executor;
	}
	
	@Bean(name = "EWAYAPI_EXECUTER")
	public Executor executor_2() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(50);
		executor.setMaxPoolSize(100);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("EWAYAPI_EXECUTER");
		executor.initialize();

		return executor;
	}


	@Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
	
	
	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper mapper =new ObjectMapper();
		return mapper;
	}

	
	@Bean
	public Gson getGson() {
		Gson json = new Gson();
		return json;
	}
	
	
	

}
