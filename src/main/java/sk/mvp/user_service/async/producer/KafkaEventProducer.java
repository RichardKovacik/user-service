package sk.mvp.user_service.async.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sk.mvp.common.event.BaseEvent;

@Service
public class KafkaEventProducer implements EventProducer {
    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventProducer.class);

    public KafkaEventProducer(KafkaTemplate<String, BaseEvent<?>> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(String topic, BaseEvent<?> event) {
        kafkaTemplate.send(topic, event.userId() ,event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        logger.info("{} Succesfully sent to: {}, offset: {}", event.metadata().correlationId(), topic, result.getRecordMetadata().offset());
                    } else {
                        logger.error("{} Exception: {}", event.metadata().correlationId() ,ex.getMessage(), ex);
                    }
                });
    }
}
