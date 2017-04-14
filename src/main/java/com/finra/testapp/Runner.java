package com.finra.testapp;

import org.springframework.boot.*;

public class Runner {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpringConfig.class, args);
    }
}