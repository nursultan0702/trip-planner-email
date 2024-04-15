package com.tripplanner.emailservice.service.impl;

import com.tripplanner.emailservice.client.UserManagementClient;
import com.tripplanner.emailservice.exception.UserNotFoundException;
import com.tripplanner.emailservice.model.UserResponse;
import com.tripplanner.emailservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserManagementClient userManagementClient;
  private final CircuitBreakerFactory cbFactory;

  @Override
  public UserResponse getUserByEmail(String email) {
    return cbFactory.create("userClientCircuitBreaker").run(
        () -> userManagementClient.getUser(email)
            .orElseThrow(() -> new UserNotFoundException(email)),
        throwable -> fallbackGetUserByEmail(email, throwable)
    );
  }

  private UserResponse fallbackGetUserByEmail(String email, Throwable t) {
    return new UserResponse(email, "Dear", "User");
  }
}

