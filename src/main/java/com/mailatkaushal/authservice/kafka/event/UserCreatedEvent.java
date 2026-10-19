package com.mailatkaushal.authservice.kafka.event;

import java.util.UUID;

public record UserCreatedEvent(UUID id, String username) {
}
