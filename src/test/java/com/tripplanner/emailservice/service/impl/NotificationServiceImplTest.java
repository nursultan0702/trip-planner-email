package com.tripplanner.emailservice.service.impl;

import com.tripplanner.emailservice.model.EmailRecord;
import com.tripplanner.emailservice.model.NotificationRecord;
import com.tripplanner.emailservice.model.PlaceRecord;
import com.tripplanner.emailservice.model.TripRecord;
import com.tripplanner.emailservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

  @Mock
  private EmailService emailService;

  private NotificationServiceImpl notificationService;

  @BeforeEach
  void setUp() {
    notificationService = new NotificationServiceImpl(emailService);
  }

  @Test
  void testProcessNotification() {
    PlaceRecord place = PlaceRecord.builder().name("name").country("Spain").build();
    PlaceRecord place2 = PlaceRecord.builder().name("name").country("Italy").build();
    Set<PlaceRecord> places = Set.of(place, place2);
    Set<String> members = Set.of("user1@example.com", "user2@example.com");

    TripRecord trip =
        new TripRecord(1L, "Summer Trip", "A fun summer trip.", null, null, members, List.of(1L, 2L));
    NotificationRecord notification = new NotificationRecord(trip, places);

    notificationService.processNotification(notification);

    verify(emailService, times(members.size())).sendHtmlMessage(any(EmailRecord.class));
  }
}
