package com.yibing.serviceapi.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public interface ServiceApi {

    @GetMapping("/service/hello/sayHello")
    public String sayHello();
}
