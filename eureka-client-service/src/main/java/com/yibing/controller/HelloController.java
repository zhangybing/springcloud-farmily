package com.yibing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yibing
 */
@RestController
@RequestMapping("/hello")
public class HelloController {
    @GetMapping("/sayHello")
    public String sayHello(){
        return "hello spring cloud";
    }
}
