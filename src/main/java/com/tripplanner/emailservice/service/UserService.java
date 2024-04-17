package com.tripplanner.emailservice.service;

import com.tripplanner.emailservice.model.UserResponse;

public interface UserService {
  UserResponse getUserByEmail(String email);
}
