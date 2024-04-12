package com.tripplanner.emailservice.service;

import com.tripplanner.emailservice.model.EmailRecord;

public interface EmailService {
  void sendHtmlMessage(EmailRecord emailRecord);
}
