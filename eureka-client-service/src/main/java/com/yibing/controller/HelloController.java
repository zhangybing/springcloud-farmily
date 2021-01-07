package com.yibing.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yibing
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Value("${server.port}")
    String port;

    @GetMapping("/sayHello")
    public String sayHello() {
        return port + "hello spring cloud";
    }
}
