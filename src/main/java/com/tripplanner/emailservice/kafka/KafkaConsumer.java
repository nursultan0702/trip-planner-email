package com.tripplanner.emailservice.kafka;

import com.tripplanner.emailservice.model.NotificationRecord;
import com.tripplanner.emailservice.service.NotificationService;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

  @Bean
  public Consumer<NotificationRecord> consume(NotificationService notificationService) {
    return message -> {
      log.info("Received message: {}", message);
      notificationService.processNotification(message);
    };
  }
}


