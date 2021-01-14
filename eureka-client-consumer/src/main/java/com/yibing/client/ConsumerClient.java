package com.yibing.client;

import com.yibing.client.callbackfactory.ConsumerClientFallBackFactory;
import com.yibing.client.fallback.ConsumerClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Administrator
 * value：服务名
 * fallback：当远程服务出现异常时，对应的降级策略（即兜底方案）
 */
//@FeignClient(value = "eureka-client-service", fallback = ConsumerClientFallBack.class)
@FeignClient(value = "eureka-client-service", fallbackFactory = ConsumerClientFallBackFactory.class)
public interface ConsumerClient {
    /**
     * 这里一定要写完整的访问路径，从根路径开始
     * 服务名只是路由：IP + 端口号
     * 注意传参的正确姿势@RequestParam
     *
     * @return
     */
    @RequestMapping("/service/sayHello")
    public String sayHello(@RequestParam("id") Integer id,
                           @RequestParam("name") String name);
}
