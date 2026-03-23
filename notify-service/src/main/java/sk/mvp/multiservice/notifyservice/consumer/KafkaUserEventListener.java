package sk.mvp.multiservice.notifyservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import sk.mvp.common.event.BaseEvent;
import sk.mvp.common.event.EventType;
import sk.mvp.common.payloads.PasswordResetPayload;
import sk.mvp.common.payloads.UserRegisteredPayload;

import static sk.mvp.common.event.EventType.USER_REGITERED_EVENT;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUserEventListener {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-event-topic", groupId = "notification-group")
    public void consumeEvent(BaseEvent<?> event) {
        try {
            log.info("{} Started processing event: {} for user: {}",
                    event.metadata().correlationId(),
                    event.eventType(),
                    event.userId());

            EventType type = EventType.valueOf(event.eventType());

            // 2. Routing
            switch (type) {
                case USER_REGITERED_EVENT -> {
                    var payload = convert(event.payload(), UserRegisteredPayload.class);
                    handleRegistration(payload, event.userId());
                }
                case PASSWORD_CHANGE_REQUESTED_EVENT -> {
                    var payload = convert(event.payload(), PasswordResetPayload.class);
                    handlePasswordReset(payload, event.userId());
                }
                case UNKNOWN_EVENT -> log.warn("{} Recieved unknown event type:",event.metadata().correlationId());
            }
        } catch (Exception e) {
            log.error("{} Exception in consuming message: {}", event.metadata().correlationId(), event.eventId(), e);
        } finally {
        }
    }

    private void handleRegistration(UserRegisteredPayload payload, String userId) {
        log.info("Registrujem usera {} s emailom {}", userId, payload.email());
        // Biznis logika...
    }

    private void handlePasswordReset(PasswordResetPayload payload, String userId) {
        log.info("Reset hesla pre email {}", payload.email());
        // Biznis logika...
    }

    // Pomocná metóda na bezpečnú konverziu
    private <T> T convert(Object payload, Class<T> clazz) {
        return objectMapper.convertValue(payload, clazz);
    }
}
