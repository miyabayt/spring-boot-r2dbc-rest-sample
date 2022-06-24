package com.bigtreetc.sample.r2dbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.bigtreetc.sample")
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
