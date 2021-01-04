package com.yibing.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/discoverclient")
public class DiscoverClientController {

    /**
     * 这个是Spring Cloud封装的客户端接口，因此方法没有，比如EurekaClient的一些方法在这里就没有，那就需要使用EurekaClient了
     */
    @Autowired
    DiscoveryClient discoveryClient;

    /**
     * 这里使用Autowired会报有多个EnrekaClient的错误
     * 原因是Spring不知道要注入哪一个，因此使用@Resource指定具体的Bean即可
     */
    @Resource(name = "eurekaClient")
    EurekaClient eurekaClient;

    /**
     * 负载均衡客户端,可以将请求较为均匀的分发到所有服务节点上
     */
    @Autowired
    LoadBalancerClient loadBalancerClient;


    /**
     * 获取所有服务名
     *
     * @return
     */
    @GetMapping("/getServices")
    public String getServices() {
        List<String> serviceList = discoveryClient.getServices();
        serviceList.forEach(val -> {
            System.out.println(val);
        });
        return "getServices";
    }

    /**
     * 根据服务名获取服务实例
     *
     * @return
     */
    @GetMapping("/getInstances")
    public Object getInstances() {
        List<ServiceInstance> serviceList = discoveryClient.getInstances("eureka-client-service");
        for (ServiceInstance service : serviceList) {
            System.out.println("host:" + service.getHost());
            System.out.println("InstancedId:" + service.getInstanceId());
            System.out.println("MetaData:" + service.getMetadata());
            System.out.println("Port" + service.getPort());
            System.out.println("Scheme" + service.getScheme());
            System.out.println("Uri:" + service.getUri());
        }
        return serviceList;
    }


    /**
     * 获取服务，并且调用服务方接口
     * 简单版本的远程服务调用，通过Eureka服务器获取url,并使用RestTemplate调用
     *
     * @return
     */
    @GetMapping("/getServiceAndRequestService")
    public Object getServiceAndRequestService() {
        //第一个参数是服务名，第二个参数是权限验证，false为关闭
        List<InstanceInfo> serviceInfo = eurekaClient.getInstancesByVipAddress("eureka-client-service", false);
        String response = null;
        if (serviceInfo.size() > 0) {
            InstanceInfo service = serviceInfo.get(0);
            if (service.getStatus() == InstanceInfo.InstanceStatus.UP) {//说明服务可用
                String url = "http://" + service.getHostName() + ":" + service.getPort() + "/service/hello/sayHello";
                System.out.println("RequestUrl:" + url);
                RestTemplate restTemplate = new RestTemplate();
                response = restTemplate.getForObject(url, String.class);
            }
        }
        return "success : " + response;
    }


    /**
     * 获取服务，并且使用LB调用服务方接口
     */
    @GetMapping("/getServiceAndRequestServiceByLb")
    public Object getServiceAndRequestServiceByLb() {
        /**
         * ribbon完成客户端的负载均衡，负载均衡策略可以选择
         * 优点：lb的choose方法会自动选择一个可用的service，把已经down掉的service剔除
         * 缺点：需要自己手动拼接hostname和port
         */
        ServiceInstance service = loadBalancerClient.choose("eureka-client-service");
        String url = "http://" + service.getHost() + ":" + service.getPort() + "/service/hello/sayHello";
        System.out.println("RequestUrl:" + url);
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }

}
