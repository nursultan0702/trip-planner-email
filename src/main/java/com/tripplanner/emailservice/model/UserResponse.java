package com.tripplanner.emailservice.model;

import lombok.Builder;

@Builder
public record UserResponse(String email, String firstName, String secondName) {
}
