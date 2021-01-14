package com.yibing.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yibing
 */
@RestController
public class HelloController {

    @Value("${server.port}")
    String port;

    private AtomicInteger count = new AtomicInteger();

    @RequestMapping("/sayHello")
    public String sayHello(HttpServletRequest request) {
        /**
         * 这里设置等待时间，是为了测试ribbon的超时机制
         */
        int a = 1 / 0;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int i = count.incrementAndGet();
        String id = request.getParameter("id");
        String name = request.getParameter("name");
        System.out.println("第"+count+"次调用");
        return port + ": hello spring cloud :---->"+id+"------"+name;
    }

}
