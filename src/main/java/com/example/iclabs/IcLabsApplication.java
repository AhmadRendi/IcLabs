package com.example.iclabs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class IcLabsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IcLabsApplication.class, args);
    }

}
