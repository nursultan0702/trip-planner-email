package com.tripplanner.emailservice.model;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record EmailRecord(String to,
                          String subject,
                          String title,
                          String name,
                          LocalDateTime start,
                          LocalDateTime end,
                          String details,
                          Set<String> locations) {
}
