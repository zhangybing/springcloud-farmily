package com.yibing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class EurekaClientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientServiceApplication.class, args);
    }

}
