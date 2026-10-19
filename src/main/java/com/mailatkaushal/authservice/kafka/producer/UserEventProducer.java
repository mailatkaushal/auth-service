package com.mailatkaushal.authservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mailatkaushal.authservice.kafka.event.UserCreatedEvent;
import com.mailatkaushal.authservice.kafka.topic.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@RequiredArgsConstructor 
@Slf4j 
public class UserEventProducer {
    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void publishUserCreated(UserCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.USER_CREATED,
                event.id().toString(),
                event
        );

        log.info("User event published {}", event);
    }
}
