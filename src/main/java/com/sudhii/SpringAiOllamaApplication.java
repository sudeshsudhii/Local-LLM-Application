package com.sudhii;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@EnableMongoAuditing
@SpringBootApplication
@ComponentScan(basePackages = {"com.sudhii"})
@EnableMongoRepositories(basePackages = {"com.sudhii.repository"})
@EntityScan(basePackages = {"com.sudhii.model"})
public class SpringAiOllamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiOllamaApplication.class, args);
    }

}
