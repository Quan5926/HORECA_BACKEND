package com.example.horecaone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;  // ← Thêm dòng này

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.horecaone.repository")  // ← Thêm dòng này để ép Spring scan repository
public class HorecaoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(HorecaoneApplication.class, args);
    }
}