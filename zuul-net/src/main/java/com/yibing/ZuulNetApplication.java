package com.yibing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author Administrator
 */


@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class ZuulNetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulNetApplication.class, args);
    }

}
