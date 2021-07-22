package com.antra.evaluation.reporting_system;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDynamoDBRepositories(basePackages = "com.antra.evaluation.reporting_system.repo")
public class ReportingSystemExcelApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportingSystemExcelApplication.class, args);
    }

}
