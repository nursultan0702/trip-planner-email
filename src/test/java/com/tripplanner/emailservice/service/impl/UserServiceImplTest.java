package com.tripplanner.emailservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tripplanner.emailservice.client.UserManagementClient;
import com.tripplanner.emailservice.exception.UserNotFoundException;
import com.tripplanner.emailservice.model.UserResponse;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

@SpringBootTest
class UserServiceImplTest {

  @Autowired
  private UserServiceImpl userService;

  @MockBean
  private UserManagementClient userManagementClient;

  @MockBean
  private CircuitBreakerFactory circuitBreakerFactory;

  @Test
  void testGetUserByEmailSuccessfully() {
    String email = "user@example.com";
    UserResponse expectedUser = new UserResponse(email, "John", "Doe");
    CircuitBreaker mockCircuitBreaker = mock(CircuitBreaker.class);

    when(userManagementClient.getUser(email)).thenReturn(Optional.of(expectedUser));
    when(circuitBreakerFactory.create(anyString())).thenReturn(mockCircuitBreaker);
    when(mockCircuitBreaker.run(any(Supplier.class), any(Function.class))).thenReturn(expectedUser);

    UserResponse result = userService.getUserByEmail(email);
    assertEquals(expectedUser, result);
  }

  @Test
  void testGetUserByEmailFallback() {
    String email = "notfound@example.com";
    CircuitBreaker mockCircuitBreaker = mock(CircuitBreaker.class);
    UserNotFoundException exception = new UserNotFoundException(email);
    UserResponse fallbackResponse = new UserResponse(email, "Dear", "User");

    when(userManagementClient.getUser(email)).thenReturn(Optional.empty());
    when(circuitBreakerFactory.create(anyString())).thenReturn(mockCircuitBreaker);
    when(mockCircuitBreaker.run(any(Supplier.class), any(Function.class))).thenAnswer(
        invocation -> {
          Supplier supplier = invocation.getArgument(0);
          Function<Throwable, UserResponse> fallback = invocation.getArgument(1);
          try {
            return supplier.get();
          } catch (Throwable t) {
            return fallback.apply(t);
          }
        });

    UserResponse result = userService.getUserByEmail(email);
    assertEquals(fallbackResponse, result);
  }

  @Test
  void testGetUserByEmailUserNotFound() {
    String email = "nonexistent@example.com";
    CircuitBreaker mockCircuitBreaker = mock(CircuitBreaker.class);

    when(userManagementClient.getUser(email)).thenReturn(Optional.empty());
    when(circuitBreakerFactory.create(anyString())).thenReturn(mockCircuitBreaker);
    when(mockCircuitBreaker.run(any(Supplier.class), any(Function.class)))
        .thenThrow(new UserNotFoundException(email));

    Exception exception =
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
    assertEquals("User with email " + email + " not found", exception.getMessage());
  }
}
