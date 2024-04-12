package com.tripplanner.emailservice.service;

import com.tripplanner.emailservice.model.NotificationRecord;

public interface NotificationService {
  void processNotification(NotificationRecord notification);
}
