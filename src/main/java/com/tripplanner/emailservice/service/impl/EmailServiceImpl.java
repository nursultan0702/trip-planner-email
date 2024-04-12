package com.tripplanner.emailservice.service.impl;

import com.tripplanner.emailservice.model.EmailRecord;
import com.tripplanner.emailservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;

  private final TemplateEngine templateEngine;

  @Override
  public void sendHtmlMessage(EmailRecord emailRecord) {

    var googleCalendarLink =
        generateGoogleCalendarLink(emailRecord.title(), emailRecord.start(), emailRecord.end(),
            emailRecord.details(), emailRecord.locations());

    var context = getContext(emailRecord, googleCalendarLink);

    var htmlContent = templateEngine.process("/email-template.html", context);

    MimeMessage message = null;
    try {
      message = getMimeMessage(emailRecord, htmlContent);
      mailSender.send(message);
      log.info("Email sent successfully");
    } catch (MessagingException e) {
      log.error("Error during building email template to record: {}, error message: {} ",
          emailRecord, e.getMessage());
    }
  }

  private static @NotNull Context getContext(EmailRecord emailRecord, String googleCalendarLink) {
    var context = new Context();
    context.setVariable("name", emailRecord.name());
    context.setVariable("googleCalendarLink", googleCalendarLink);
    context.setVariable("locations", emailRecord.locations());
    return context;
  }

  private @NotNull MimeMessage getMimeMessage(EmailRecord emailRecord, String htmlContent)
      throws MessagingException {
    var message = mailSender.createMimeMessage();
    var helper = new MimeMessageHelper(message, true);
    helper.setFrom("nurs_sadyk@mail.ru");
    helper.setTo(emailRecord.to());
    helper.setSubject(emailRecord.subject());
    helper.setText(htmlContent, true);
    return message;
  }

  public String generateGoogleCalendarLink(String title, LocalDateTime start, LocalDateTime end,
                                           String details, Set<String> locations) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    String startTime = formatter.format(start);
    String endTime = formatter.format(end);

    return "https://www.google.com/calendar/render?action=TEMPLATE" +
        "&text=" + URLEncoder.encode(title, StandardCharsets.UTF_8) +
        "&dates=" + startTime + "/" + endTime +
        "&details=" + URLEncoder.encode(details, StandardCharsets.UTF_8) +
        "&location=" + URLEncoder.encode(String.join(",", locations), StandardCharsets.UTF_8) +
        "&sf=true&output=xml";
  }

}
