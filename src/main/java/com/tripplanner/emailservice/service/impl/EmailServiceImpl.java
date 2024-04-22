package com.tripplanner.emailservice.service.impl;

import com.tripplanner.emailservice.model.EmailRecord;
import com.tripplanner.emailservice.model.UserResponse;
import com.tripplanner.emailservice.service.EmailService;
import com.tripplanner.emailservice.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
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
  private final UserService userService;

  @Override
  public void sendHtmlMessage(EmailRecord emailRecord) {
    try {
      var htmlContent = generateEmailContent(emailRecord);
      sendEmail(emailRecord, htmlContent);
      log.info("Email sent successfully");
    } catch (MessagingException e) {
      log.error("Error during building email template to record: {}, error message: {} ",
          emailRecord, e.getMessage());
    }
  }

  private String generateEmailContent(EmailRecord emailRecord) {
    var googleCalendarLink = generateGoogleCalendarLink(emailRecord);
    var user = userService.getUserByEmail(emailRecord.to());
    var context = getContext(user, emailRecord, googleCalendarLink);

    return templateEngine.process("/email-template.html", context);
  }

  private void sendEmail(EmailRecord emailRecord, String htmlContent) throws MessagingException {
    MimeMessage message = getMimeMessage(emailRecord, htmlContent);
    mailSender.send(message);
  }

  protected static @NotNull Context getContext(UserResponse user, EmailRecord emailRecord,
                                               String googleCalendarLink) {
    var context = new Context();
    context.setVariable("username", String.format("%s %s", user.firstName(), user.secondName()));
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

  protected String generateGoogleCalendarLink(EmailRecord emailRecord) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    String startTime = formatter.format(emailRecord.start());
    String endTime = formatter.format(emailRecord.end());

    return "https://www.google.com/calendar/render?action=TEMPLATE" +
        "&text=" + URLEncoder.encode(emailRecord.title(), StandardCharsets.UTF_8) +
        "&dates=" + startTime + "/" + endTime +
        "&details=" + URLEncoder.encode(emailRecord.details(), StandardCharsets.UTF_8) +
        "&location=" +
        URLEncoder.encode(String.join(",", emailRecord.locations()), StandardCharsets.UTF_8) +
        "&sf=true&output=xml";
  }

}
