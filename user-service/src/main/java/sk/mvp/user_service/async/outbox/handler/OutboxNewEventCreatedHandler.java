package sk.mvp.user_service.async.outbox.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sk.mvp.common.event.BaseEvent;
import sk.mvp.user_service.async.outbox.dto.OutboxTriggerEvent;
import sk.mvp.user_service.async.outbox.entity.OutboxEvent;
import sk.mvp.user_service.async.outbox.service.IOutBoxService;
import sk.mvp.user_service.async.producer.IEventProducer;
import sk.mvp.user_service.async.producer.KafkaIEventProducer;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxNewEventCreatedHandler {
    private final IOutBoxService outBoxService;
    private final IEventProducer eventProducer;

    @Async("outboxExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public<T> void handleNewOutboxEventCreated(OutboxTriggerEvent event) {
        log.info("Handling internal event new outbox created: {}", event.eventId());
        try {
            BaseEvent<T> deserializedPayload = outBoxService.findOutboxById(event.eventId());

            eventProducer.produce(deserializedPayload.destinationTopic(), deserializedPayload);
            log.info("[{}] Event {} sucessefully to kafka", deserializedPayload.metadata().correlationId(), event.eventId());

            int rowsChanged = outBoxService.markAsProcessed(event.eventId());
            if (rowsChanged == 0) {
                log.info("Outbox event {} successfully marked as PROCESSED in DB.", event.eventId());
            } else {
                log.warn("Outbox event {} status update skipped. Row was not in PENDING state or does not exist. (Possible race condition)",
                        event.eventId());

            }

        }catch (Exception e) {
            log.error("Immediate send failed for {}. Leaving outbox event in PENDING state for retry. Error: {},",
                    event.eventId(), e.getMessage(), e);
        }

    }
}
