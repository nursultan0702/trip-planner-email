package com.tripplanner.emailservice.client;

import com.tripplanner.emailservice.model.UserResponse;
import java.util.Optional;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "trip-planner-auth")
public interface UserManagementClient {

  @GetMapping("/api/v1/user/{email}")
  Optional<UserResponse> getUser(@PathVariable("email") String email);
}
