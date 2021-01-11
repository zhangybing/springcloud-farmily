package com.yibing.client.fallback;

import com.yibing.client.ConsumerClient;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class ConsumerClientFallBack implements ConsumerClient {

    @Override
    public String sayHello(Integer id, String name) {
        return "sayHello降级了......";
    }
}
