package com.carrent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 汽车租赁管理系统后端启动类
 */
@SpringBootApplication
@MapperScan("com.carrent.mapper")
public class CarRentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentApplication.class, args);
    }
}
