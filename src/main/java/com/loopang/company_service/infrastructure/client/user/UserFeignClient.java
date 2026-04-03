package com.loopang.company_service.infrastructure.client.user;

import com.loopang.company_service.infrastructure.client.user.dto.UserResponse;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserFeignClient {

  @GetMapping("/api/v1/users/{userId}")
  UserResponse getUser(@PathVariable UUID userId);
}