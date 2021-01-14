package com.yibing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
//开启Hystrix的监控

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@EnableFeignClients
@EnableHystrixDashboard
@EnableCircuitBreaker
@SpringBootApplication
public class EurekaClientConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientConsumerApplication.class, args);
    }

    /**
     * 把RestTemplate定义为单例
     *
     * 使用loadBalancerClient的话，不需要这里的LoadBalanced注解
     * 这里加上这个注解表示RestTemplate集成的Ribon负载均衡策略
     *
     * @return
     */
    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    /**
     * 自定义Ribbon的负载均衡策略
     */
//    @Bean
//    public IRule myRule(){
//        //return new RetryRule();
//        return new RandomRule();
//    }

}
