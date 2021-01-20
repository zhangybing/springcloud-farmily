package com.yibing;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 */
@EnableAdminServer
@SpringBootApplication
public class SpringCloudAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudAdminApplication.class, args);
    }

}
