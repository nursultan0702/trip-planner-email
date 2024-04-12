package com.tripplanner.emailservice.model;

import java.util.Set;
import lombok.Builder;

@Builder
public record NotificationRecord(
        TripRecord trip,
        Set<PlaceRecord> places) {
}
