package com.antra.evaluation.reporting_system;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDynamoDBRepositories(basePackages = "com.antra.evaluation.reporting_system.repo")
public class ReportingSystemPDFApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportingSystemPDFApplication.class, args);
    }

}
