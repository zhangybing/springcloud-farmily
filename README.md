# springcloud-farmily **说明**
Spring Cloud各个组件应用的demo

### 为什么需要服务治理

微服务架构下，会有 n 个服务在运行，服务于服务之间可能存在相互调用的关系，随着服务数量的增加，需要对所有服务进行管理，即服务提供者往注册中心注册，服务消费者从注册中心获取服务列表然后调用，另外如果涉及到动态的容量调整，那么就需要自动的服务发现和服务注销

### 注册中心的基本模型

访问注册中心的客户端程序一般会嵌入在服务提供者和服务消费者内部。

在服务启动时，服务提供者通过内部的注册中心客户端程序自动将自身注册到注册中心，

服务消费者的注册中心客户端程序则可以从注册中心中获取那些已经注册的服务实例信息。

注册中心的基本模型参考下图

![图片](https://uploader.shimo.im/f/tdCFfuh9C1Yon7ap.png!thumbnail?fileGuid=wt8gy9dwCxjXRQRT)

**路由缓存表：**为了提高服务路由的效率和容错性，服务消费者配备 l 了缓存机制以加速服务路由。更重要的是，当服务注册中心不可用时，服务消费者可以利用本地缓存路由实现对现有服务的可靠调用


### EureKa 单机搭建

#### EureKa 高可用集群搭建

1. **准备**

准备 2 个节点部署 eureka，也可以单机部署

修改本机 host 文件（路径：C:\Windows\System32\drivers\etc），绑定一个主机名，单机部署时使用 ip 地址会有问题

```powershell
127.0.0.1 euk1.com
127.0.0.1 euk2.com
```
2. **配置文件**

**文件 1**

```yaml
eureka:
  client:
    #是否将自己注册到 Eureka Server,默认为 true，由于当前就是 server，故而设置成 false，表明该服务不会向 eureka 注册自己的信息
    register-with-eureka: true
    #是否从 eureka server 获取注册信息，由于单节点，不需要同步其他节点数据，用 false
    fetch-registry: true
    #设置服务注册中心的 URL，用于 client 和 server 端交流
    service-url:
      #带有 zone 的原因是最初该项目在亚马逊上部署，因此有部分配置沿用了早期的配置，配置多个地址的话就可以向多个节点去注册
      defaultZone: http://euk2.com:7002/eureka/
  instance:
    hostname: euk1.com
management:
  endpoint:
    shutdown:
      enabled: true
server:
  port: 7001
spring:
  application:
    name:EurekaServer
```
配置文件 2
```yaml
eureka:
  client:
    #是否将自己注册到 Eureka Server,默认为 true，由于当前就是 server，故而设置成 false，表明该服务不会向 eureka 注册自己的信息
    register-with-eureka: true
    #是否从 eureka server 获取注册信息，由于单节点，不需要同步其他节点数据，用 false
    fetch-registry: true
    #设置服务注册中心的 URL，用于 client 和 server 端交流
    service-url:
      #带有 zone 的原因是最初该项目在亚马逊上部署，因此有部分配置沿用了早期的配置，配置多个地址的话就可以向多个节点去注册
      defaultZone: http://euk1.com:7001/eureka/
  instance:
    hostname: euk2.com
management:
  endpoint:
    shutdown:
      enabled: true
server:
  port: 7002
spring:
  application:
    name:EurekaServer
```
3. 启动类加入@EnableEurekaServer 注解来标识这是一个 Eureka 服务器
```java
@EnableEurekaServer
@SpringBootApplication
public class SpringEurekaServerApplication {
  public static void main(String[] args) {
      SpringApplication.run(SpringEurekaServerApplication.class, args);
  }
}
```
4. 用两个配置文件分别启动两个服务，如下图就是集群搭建成功![图片](https://uploader.shimo.im/f/GN6dZ6jJZYtEizq3.png!thumbnail?fileGuid=wt8gy9dwCxjXRQRT)

![图片](https://uploader.shimo.im/f/zKjpqzpmNkumu6tF.png!thumbnail?fileGuid=wt8gy9dwCxjXRQRT)

### 注册中心原理

* Register（服务注册）

想要参与服务注册发现的实例首先需要向 Eureka 服务器注册信息，注册在第一次心跳发生时提交

* Renew(续租、心跳)

由客户端向服务端发起，每 30 秒一次心跳来完成续租，告诉服务端这个客户端还活着，如果服务端在 90 秒之内没有看到心跳，就把这个客户端从注册列表中移除

* Fetch Registry(获取服务列表信息)

Eureka 客户端从服务器获取注册表信息并将其缓存在本地。之后，客户端使用这些信息来查找其他服务。

通过获取上一个获取周期和当前获取周期之间的增量更新，可以定期(**每 30 秒**)更新此信息。

节点信息在服务器中保存的时间更长(大约 3 分钟)，因此获取节点信息时可能会再次返回相同的实例。Eureka 客户端自动处理重复的信息。

在获得增量之后，Eureka 客户机通过比较服务器返回的实例计数来与服务器协调信息，如果由于某种原因信息不匹配，则再次获取整个注册表信息。

* Cancel（下线通知）

Eureka 客户端在关闭时向 Eureka 服务器发送取消请求。这将从服务器的实例注册表中删除实例，从而有效地将实例从通信量中取出。

* Time Lag（同步时间延迟）

来自 Eureka 客户端的所有操作可能需要一段时间才能反映到 Eureka 服务器上，然后反映到其他 Eureka 客户端上。这是因为 eureka 服务器上的有效负载缓存，它会定期刷新以反映新信息。Eureka 客户端还定期地获取增量。因此，更改传播到所有 Eureka 客户机可能需要 2 分钟。

* Communication mechanism

通讯机制是 Http 协议下的 Rest 请求，默认情况下 Eureka 使用 Jersey 和 Jackson 以及 JSON 完成节点间的通讯

### 服务提供方注册

* web应用引入eureka-client支持
```xml
<dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
* 启动类加入@EnableEurekaClient 注解，该注解用于表明当前服务就是一个 Eureka 客户端，这样该服务就可以自动注册到 Eureka 服务器
```java
@EnableEurekaClient
@SpringBootApplication
public class EurekastarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekastarterApplication.class, args);
    }
}
```
* 配置文件
```yaml
eureka:
  client:
    service-url:
      #注册中心的访问地址
      defaultZone: http://euk1.com:7001/eureka/
server:
  port: 80
# 注意多个相同应用的 name 要保持一致
spring:
  application:
    name: provider
```
![图片](https://uploader.shimo.im/f/b0NwHsGey2pgDxsj.png!thumbnail?fileGuid=wt8gy9dwCxjXRQRT)

### 服务消费方注册

* web 应用引入 eureka-client 支持
* 配置文件
```yaml
eureka:
  client:
    service-url:
      #注册中心的访问地址
      defaultZone: http://euk1.com:7001/eureka/
server:
  port: 90
spring:
  application:
    name: consumer
```
#### 如何理解服务者和消费者

对于 Eureka 而言，微服务的提供者和消费者都是它的客户端，其中**服务提供者关注服务注册、服务续约和服务下线**等功能，而**服务消费者关注于服务信息的获取**。同时，对于服务消费者而言，为了提高服务获取的性能以及在注册中心不可用的情况下继续使用服务，一般都还会具有缓存机制

#### 极简版远程服务调用

```java
/**
 * 极简版本远程服务调用，通过 eureka 服务器
 *
 * @return
 */
@GetMapping("/callService")
public Object callService() {
    //获取服务元数据信息
    List<InstanceInfo> ins = eurekaClient.getInstancesByVipAddress("provider", false);
    if (ins.size() > 0) {
        InstanceInfo info = ins.get(0);
        //拼装访问路径
        if (info.getStatus() == InstanceInfo.InstanceStatus.UP) {
            String url = "http://" + info.getHostName() + ":" + info.getPort() + "/gethi";
            //发起服务调用
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);
        }
    }
    return "xxoo";
}
```
#### 负载均衡式远程服务调用

```java
/**
 * 通过负载均衡的方式获取服务列表，并调用接口
 * @return
 */
@GetMapping("/loadBlance")
public Object loadBlance() {
    //通过 loadBlance 拿到的服务列表，会自动把状态已经 DOWN 的服务筛选掉，返回的服务全部是可用的
    //choose 有负载均衡的策略
    ServiceInstance instance = loadBalancerClient.choose("provider");
    String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/gethi";
    RestTemplate restTemplate = new RestTemplate();
    String response = restTemplate.getForObject(url, String.class);
    return response;
}
```
### 自我保护机制

#### 机制

Eureka 在 CAP 理论当中是属于 AP， 也就说当产生网络分区时，Eureka 保证系统的可用性，但不保证系统里面数据的一致性

默认开启，服务器端容错的一种方式，即短时间心跳不到达仍不剔除服务列表里的节点，在 Eureka 页面控制台会有如下提示：

```plain
EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE. 
```
默认情况下，Eureka Server 在一定时间内，没有接收到某个微服务心跳，会将某个微服务注销 (90S）。但是当网络故障时，微服务与 Server 之间无法正常通信，上述行为就非常危险，因为微服务正常，不应该注销。

Eureka Server 通过自我保护模式来解决整个问题，当 Server 在短时间内丢失过多客户端时，那么 Server 会进入自我保护模式，会保护注册表中的微服务不被注销掉。当网络故障恢复后，退出自我保护模式。

**思想：宁可保留健康的和不健康的，也不盲目注销任何健康的服务。**

#### 自我保护触发

**客户端每分钟续约数量小于客户端总数的 85%时会触发保护机制**

自我保护机制的触发条件： （当每分钟心跳次数( renewsLastMin ) 小于 numberOfRenewsPerMinThreshold 时，并且开启自动保护模式开关( eureka.server.enable-self-preservation = true ) 时，触发自我保护机制，不再自动过期租约。） numberOfRenewsPerMinThreshold = expectedNumberOfRenewsPerMin * 续租百分比( eureka.server.renewalPercentThreshold, 默认 0.85 ) expectedNumberOfRenewsPerMin = 当前注册的应用实例数 x 2 为什么乘以 2： 默认情况下，注册的应用实例每半分钟续租一次，那么一分钟心跳两次，因此 x 2。

服务实例数：10 个，期望每分钟续约数：10 * 2=20，期望阈值：20*0.85=17，自我保护少于 17 时 触发。

#### 关闭自我保护

```plain
eureka.server.enable-self-preservation=false 
```
![图片](https://uploader.shimo.im/f/MjgHFhNCXAmKNRVf.png!thumbnail?fileGuid=wt8gy9dwCxjXRQRT)

### EureKa 手动配置服务上下线

在 client 端配置：将自己真正的健康状态传播到 server。

```plain
eureka:
  client:
    healthcheck:
      enabled: true
```
#### Client 端配置 Actuator

```plain
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```
#### 必须要实现的 Health 接口类

```java
@Service
public class HealthStatusService implements HealthIndicator {
    private Boolean status = true;
    public void setStatus(boolean status) {
        this.status = status;
    }
    public Boolean getStatus() {
        return this.status;
    }
    @Override
    public Health health() {
        if (status) {
            return new Health.Builder().up().build();
        }
        return new Health.Builder().down().build();
    }
}
```
改变健康状态的 Controller
```java
@RestController
public class ServiceHealthController {
    @Autowired
    HealthStatusService healthService;
    /**
     * 手动控制服务上下线
     *
     * @param status
     * @return
     */
    @RequestMapping("/setServiceHealth")
    public Boolean setServiceHealth(@RequestParam("status") Boolean status){
        healthService.setStatus(status);
        return healthService.getStatus();
    }
}
```
### 健康检查 Actuator

#### 开启监控

```plain
<!-- 上报节点信息 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
#### 开启所有端点

在 application.yml 中加入如下配置信息

*代表所有节点都加载

```plain
#开启所有端点
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

### 开启 Eureka 服务安全验证

1. 引入依赖
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-security</artifactId>
</dependency>
```
2. 设置账号密码
```plain
spring:
 security:
   user:
     name: zhangyibing
     password: zyb123456
```
3. 如果服务注册报错,是默认开启了防止跨域攻击 ,需要手动关闭
```plain
Root name 'timestamp' does not match expected ('instance') for type [simple
```
在服务端增加配置类
```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        super.configure(http);
    }
}
```
### RestTemplate 整合 Ribbon 服务注册与服务负载均衡消费

在启动类中加入@Bean 和@LoadBalanced 注解

```plain
@Bean
@LoadBalanced//自动负载均衡
RestTemplate  getRestTemplate(){
    return new RestTemplate();
}
```
使用范例：
```java
/**
 * 通过负载均衡的方式获取服务列表，并自动处理 URL，并远程调用服务接口
 * 在初始化 restTemplate 的时候给@LoadBalanced 的注解
 * @return
 */
@GetMapping("/autoBuildUrl")
public Object autoBuildUrlLoadBalanced() {
    //自动处理 URL，其中 provider 是服务名，gethi 是接口名
    String url = "http://provider/gethi";
    String response = restTemplate.getForObject(url, String.class);
    return response;
}
```
### 通过 @RibbonClient 注解自定义负载均衡策略

默认情况下，Ribbon 使用的是轮询策略，我们无法控制具体生效的是哪种负载均衡算法。但在有些场景下，我们就需要对负载均衡这一过程进行更加精细化的控制，这时候就可以用到 @RibbonClient 注解。Spring Cloud Netflix Ribbon 提供 @RibbonClient 注解的目的在于通过该注解声明自定义配置，从而来完全控制客户端负载均衡行为。



