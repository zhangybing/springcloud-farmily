package com.yibing.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yibing
 */
@RestController
public class HelloController {

    @Value("${server.port}")
    String port;

    @RequestMapping("/sayHello")
    public String sayHello(HttpServletRequest request) {
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        return port + ": hello spring cloud :---->"+id+"------"+name;
    }

}
