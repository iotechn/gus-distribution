package com.dobbinsoft.gus.distribution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.dobbinsoft.gus.distribution.client")
@SpringBootApplication(scanBasePackages = "com.dobbinsoft.gus")
public class GusDistributionApplication {

    public static void main(String[] args) {
        IoC.INSTANCE = SpringApplication.run(GusDistributionApplication.class, args);
    }

}
