package com.loopang.company_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(
    // FeignClient 인터페이스가 위치한 패키지를 명시적으로 지정
    basePackages = "com.loopang.company_service.infrastructure.client"
)
public class CompanyServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CompanyServiceApplication.class, args);
  }
}
