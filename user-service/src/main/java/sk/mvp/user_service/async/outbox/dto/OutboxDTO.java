package sk.mvp.user_service.async.outbox.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import sk.mvp.user_service.async.outbox.entity.OutBoxStatus;

import java.time.Instant;
import java.util.UUID;

@Builder
@Jacksonized
 public record OutboxDTO(
        UUID eventId,
        String correlationId,
        String eventType,
        String aggregateId,
        String payload, // The serialized JSON string
        String destinationTopic,
        OutBoxStatus status,
        int retryCount,
        Instant nextRetryAt
) {}