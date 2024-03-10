package com.orange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OrangeShopBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrangeShopBeApplication.class, args);
    }

}
