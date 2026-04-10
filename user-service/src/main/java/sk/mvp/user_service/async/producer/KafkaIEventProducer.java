package sk.mvp.user_service.async.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sk.mvp.common.event.BaseEvent;
import sk.mvp.user_service.common.exception.KafkaProductionException;

import java.util.concurrent.TimeUnit;

@Service
public class KafkaIEventProducer implements IEventProducer {
    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;

    public KafkaIEventProducer(KafkaTemplate<String, BaseEvent<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
//    @Async("kafkaExecutor")
    public void produce(String topic, BaseEvent<?> event) {
        try {
            kafkaTemplate.send(topic, event.userId(), event).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            // Tu chybu LEN zabalíš a vyhodíš. Logovanie necháš na Handler.
            // Týmto odstrániš duplicitu v logoch.
            throw new KafkaProductionException("Kafka send failed for event: " + event.eventId(), e);
        }

    }
}
