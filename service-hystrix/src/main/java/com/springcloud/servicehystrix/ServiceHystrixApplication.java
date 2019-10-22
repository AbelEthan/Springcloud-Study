package com.springcloud.servicehystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

@SpringBootApplication
@EnableCircuitBreaker
public class ServiceHystrixApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHystrixApplication.class, args);
    }
}
