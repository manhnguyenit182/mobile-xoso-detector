package com.example.xoso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XosoApplication {

  public static void main(String[] args) {
    SpringApplication.run(XosoApplication.class, args);

  }

}
