package com.yibing.controller;

import com.yibing.client.ConsumerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/openFeign")
public class OpenFeignController {
    @Value("${server.port}")
    String port;

    @Autowired
    ConsumerClient consumerApi;

    @GetMapping("/helloOpenFeign")
    public String helloOpenFeign(@RequestParam("id") Integer id,@RequestParam("name") String name) {
        String prifx = "Consumer:"+port;
        return prifx+ "--------->"+consumerApi.sayHello(id,name);
    }
}
