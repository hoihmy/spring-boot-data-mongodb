package com.bezkoder.spring.data.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class SpringBootDataMongodbApplication {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .messageConverters(new StringHttpMessageConverter(), new MappingJackson2HttpMessageConverter())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDataMongodbApplication.class, args);
    }

}
