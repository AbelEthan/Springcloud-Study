package com.springcloud.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

@SpringBootApplication
public class ClientApplication {

    private Logger log = LoggerFactory.getLogger(ClientApplication.class);

    private final ContextRefresher contextRefresher;

    @Autowired
    public ClientApplication(ContextRefresher contextRefresher){
        this.contextRefresher = contextRefresher;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }

    @Scheduled(fixedRate = 1000L)
    public void update(){
        Set<String> keys = contextRefresher.refresh();
        if (!keys.isEmpty()){
            log.info("本次更新的配置项：" + keys);
        }
    }
}
