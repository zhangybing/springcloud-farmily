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
     * 负载均衡客户端,可以将请求较为均匀的分发到所有服务节点上
     */
    @Autowired
    LoadBalancerClient loadBalancerClient;

    @GetMapping("/useRestAndRibbon")
    public Object useRestAndRibbon() {
        return null;
    }
}
