package com.travel;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.travel.mapper")
@EnableScheduling
public class TravelProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelProjectApplication.class, args);
    }

}
