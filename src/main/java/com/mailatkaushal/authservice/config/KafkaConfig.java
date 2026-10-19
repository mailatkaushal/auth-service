package com.mailatkaushal.authservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.mailatkaushal.authservice.kafka.topic.KafkaTopics;

@Configuration 
public class KafkaConfig {

    @Bean 
    public NewTopic userCreatedTopic() {
        return TopicBuilder
                .name(KafkaTopics.USER_CREATED)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
