package com.pro.snap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@SpringBootApplication(scanBasePackages="com.pro.snap.controller")
@MapperScan("com.pro.snap.mapper")
public class snapApplication {

    public static void main(String[] args) {
        SpringApplication.run(snapApplication.class, args);
    }

}