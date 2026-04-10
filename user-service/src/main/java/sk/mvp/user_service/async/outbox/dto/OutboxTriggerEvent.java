package sk.mvp.user_service.async.outbox.dto;

import java.util.UUID;

public record OutboxTriggerEvent(UUID eventId) {
}
