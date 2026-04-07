package com.loopang.company_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableFeignClients는 common 라이브러리(FeignConfig)에서 basePackages = "com.loopang"으로
// 이미 등록되므로 여기 명시하면 같은 FeignClient가 두 번 스캔되어 빈 중복 등록 에러 발생.
@SpringBootApplication
public class CompanyServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(CompanyServiceApplication.class, args);
  }
}
