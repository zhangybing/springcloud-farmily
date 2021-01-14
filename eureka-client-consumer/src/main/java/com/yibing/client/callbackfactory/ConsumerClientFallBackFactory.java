package com.yibing.client.callbackfactory;

import com.yibing.client.ConsumerClient;
import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class ConsumerClientFallBackFactory implements FallbackFactory<ConsumerClient> {
    /**
     * Returns an instance of the fallback appropriate for the given cause
     *
     * @param cause 这个异常既包括本地的异常，也包括远端服务的异常
     */
    @Override
    public ConsumerClient create(Throwable cause) {
        return new ConsumerClient() {
            @Override
            public String sayHello(Integer id, String name) {
                cause.printStackTrace();
                if (cause instanceof FeignException.InternalServerError) {
                    return "sayHello 降级了，原因是远程服务器500：" + cause.getLocalizedMessage();
                }else if(cause instanceof RuntimeException){
                    return "sayHello请求时异常，降级...";
                }
                return "sayHello 降级了，原因是远程服务器500";
            }
        };
    }
}
