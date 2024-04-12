package com.tripplanner.emailservice.model;

import java.util.List;
import java.util.Set;
import lombok.Builder;

@Builder
public record PlaceRecord(Long placeId,
                          Set<Long> tripIds,
                          String country,
                          String city,
                          String name,
                          List<String> images) {
}
