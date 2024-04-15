package com.tripplanner.emailservice.client;

import com.tripplanner.emailservice.model.UserResponse;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-management", url = "http://localhost:8080/api/")
public interface UserManagementClient {

  @GetMapping("/v1/user/{email}")
  Optional<UserResponse> getUser(@PathVariable("email") String email);
}
