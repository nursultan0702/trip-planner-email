package com.tripplanner.emailservice.service.impl;

import com.tripplanner.emailservice.model.EmailRecord;
import com.tripplanner.emailservice.model.NotificationRecord;
import com.tripplanner.emailservice.model.PlaceRecord;
import com.tripplanner.emailservice.model.TripRecord;
import com.tripplanner.emailservice.service.EmailService;
import com.tripplanner.emailservice.service.NotificationService;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final EmailService emailService;

  @Override
  public void processNotification(NotificationRecord notification) {
    TripRecord trip = notification.trip();
    Set<PlaceRecord> places = notification.places();

    Set<String> emailsToSend = trip.members();

    emailsToSend.forEach(email -> {
      var emailRecord = createEmailRecord(email, trip, places);
      emailService.sendHtmlMessage(emailRecord);
    });
  }

  private static EmailRecord createEmailRecord(String email, TripRecord trip,
                                            Set<PlaceRecord> places) {
    return EmailRecord.builder()
        .to(email)
        .start(trip.startDate())
        .end(trip.endDate())
        .subject(trip.name())
        .title(trip.name())
        .details(trip.description())
        .locations(getLocations(places))
        .build();
  }

  private static @NotNull Set<String> getLocations(Set<PlaceRecord> places) {
    return places.stream().map(PlaceRecord::country).collect(
        Collectors.toSet());
  }
}
