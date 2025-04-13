package com.faucetproject.App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.faucetproject")
public class FaucetApplication {
    public static void main(String[] args) {
        SpringApplication.run(FaucetApplication.class, args);
    }
}
