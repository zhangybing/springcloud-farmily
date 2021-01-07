package com.yibing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/restAndRibbon")
public class RestTemplateAndRibbonController {

    @Autowired
    RestTemplate restTemplate;

    /**
     * RestTemplate集成Ribbon，即不用手动拼host和port了
     * 只需要服务名+接口名即可
     * @return
     */
    @GetMapping("/restTemplateHasRibbon")
    public Object restTemplateHasRibbon() {
        /**
         * ribbon完成客户端的负载均衡，负载均衡策略可以选择
         * 优点：lb的choose方法会自动选择一个可用的service，把已经down掉的service剔除
         * 缺点：需要自己手动拼接hostname和port
         */
        String url = "http://eureka-client-service/service/hello/sayHello";
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }
}
