package com.tripplanner.emailservice.service.impl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tripplanner.emailservice.model.EmailRecord;
import com.tripplanner.emailservice.model.NotificationRecord;
import com.tripplanner.emailservice.model.PlaceRecord;
import com.tripplanner.emailservice.model.TripRecord;
import com.tripplanner.emailservice.service.EmailService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class NotificationServiceImplTest {

  @Mock
  private EmailService emailService;

  private NotificationServiceImpl notificationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    notificationService = new NotificationServiceImpl(emailService);
  }

  @Test
  void testProcessNotification() {
    PlaceRecord place = PlaceRecord.builder().name("name").country("Spain").build();
    PlaceRecord place2 = PlaceRecord.builder().name("name").country("Italy").build();
    // Arrange
    Set<PlaceRecord> places = new HashSet<>();
    places.add(place);
    places.add(place2);

    Set<String> members = new HashSet<>();
    members.add("user1@example.com");
    members.add("user2@example.com");

    TripRecord trip =
        new TripRecord(1L, "Summer Trip", "A fun summer trip.", null, null, members, List.of(1L, 2L));
    NotificationRecord notification = new NotificationRecord(trip, places);

    // Act
    notificationService.processNotification(notification);

    // Assert
    verify(emailService, times(members.size())).sendHtmlMessage(any(EmailRecord.class));
  }
}
