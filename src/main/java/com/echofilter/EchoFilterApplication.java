package com.echofilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EchoFilterApplication {

    public static void main(String[] args) {
        System.out.println("EchoFilterApplication starts to run");
        SpringApplication.run(EchoFilterApplication.class, args);
    }

}
