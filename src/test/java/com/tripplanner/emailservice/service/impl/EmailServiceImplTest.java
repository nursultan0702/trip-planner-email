package com.tripplanner.emailservice.service.impl;

import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tripplanner.emailservice.model.EmailRecord;
import com.tripplanner.emailservice.model.UserResponse;
import com.tripplanner.emailservice.service.UserService;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

class EmailServiceImplTest {

  private EmailRecord emailRecord;
  private UserResponse user;


  @BeforeEach
  void setUp() {
    user = new UserResponse("john.doe@mail.com", "John", "Doe");
    emailRecord = new EmailRecord(
        "john.doe@mail.com",
        "Trip Invite",
        "USA trip",
        "NY,SA trip",
        LocalDateTime.now(),
        LocalDateTime.now().plusYears(1), "Visit NY, SA",
        new HashSet<>(List.of("Board Room")));
  }

  @Test
  void testSendHtmlMessage() {
    JavaMailSender mockMailSender = mock(JavaMailSender.class);
    TemplateEngine mockTemplateEngine = mock(TemplateEngine.class);
    UserService mockUserService = mock(UserService.class);
    EmailServiceImpl service =
        new EmailServiceImpl(mockMailSender, mockTemplateEngine, mockUserService);
    MimeMessage mockMimeMessage = mock(MimeMessage.class);

    when(mockUserService.getUserByEmail("john.doe@mail.com")).thenReturn(user);
    when(mockTemplateEngine.process(anyString(), any(Context.class))).thenReturn(
        "<html>Email Content</html>");
    when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
    doNothing().when(mockMailSender).send(any(MimeMessage.class));

    service.sendHtmlMessage(emailRecord);

    verify(mockMailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void testGenerateGoogleCalendarLink() {
    EmailServiceImpl service = new EmailServiceImpl(null, null, null);
    Set<String> locations = new HashSet<>(Arrays.asList("New York", "SA"));
    LocalDateTime start = LocalDateTime.of(2024, 4, 20, 15, 0);
    LocalDateTime end = LocalDateTime.of(2024, 4, 20, 16, 0);
    EmailRecord emailRecord2 =
        new EmailRecord("to", "", "Trip", "", start, end, "Trip USA", locations);

    String expectedLink = "https://www.google.com/calendar/render?action=TEMPLATE"
        + "&text=" + URLEncoder.encode("Trip", StandardCharsets.UTF_8)
        + "&dates=20240420T150000Z/20240420T160000Z"
        + "&details=" + URLEncoder.encode("Trip USA", StandardCharsets.UTF_8)
        + "&location=" + URLEncoder.encode("New York,SA", StandardCharsets.UTF_8)
        + "&sf=true&output=xml";

    String link = service.generateGoogleCalendarLink(emailRecord2);
    assertEquals(expectedLink, link);
  }

  @Test
  void testGetContext() {

    String calendarLink = "https://example.com";

    Context context = EmailServiceImpl.getContext(user, emailRecord, calendarLink);

    assertEquals("John Doe", context.getVariable("username"));
    assertEquals("NY,SA trip", context.getVariable("name"));
    assertEquals(calendarLink, context.getVariable("googleCalendarLink"));
    assertEquals(singleton("Board Room"), context.getVariable("locations"));
  }


}