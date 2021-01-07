package com.yibing;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RetryRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

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
