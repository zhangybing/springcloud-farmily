package com.yibing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/restTemplate")
public class RestTemplateController {

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/getForObject")
    public Object getForObject() {
        String url = "http://eureka-client-service/service/hello/sayHello";
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }
}
