package sk.mvp.user_service.async.producer.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sk.mvp.common.event.BaseEvent;
import sk.mvp.user_service.async.producer.IEventProducer;
import sk.mvp.user_service.common.exception.KafkaProductionException;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaIEventProducer implements IEventProducer {
    private final KafkaTemplate<String, BaseEvent<?>> kafkaTemplate;
    private final KafkaAdmin kafkaAdmin;


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

    @Override
    public boolean isBrokerUp() {
        Properties props = new Properties();
        // Skopírujeme tvoju existujúcu konfiguráciu (adresy brokerov atď.)
        props.putAll(kafkaAdmin.getConfigurationProperties());

        // KĽÚČOVÁ ČASŤ: Skrátime interné čakanie Kafky
        props.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, "5000"); // Celkový čas na API volanie
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "1500");     // Timeout jednej sieťovej požiadavky
        props.put(AdminClientConfig.RETRIES_CONFIG, "0");

        // Vytvorenie AdminClienta z existujúcej konfigurácie Springu
        try (AdminClient adminClient = AdminClient.create(props)) {
            // Skúsime získať clusterId - najrýchlejšia operácia na overenie spojenia
            adminClient.describeCluster().clusterId().get(5, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.warn("Kafka broker is down or unreachable.", e);
            return false;
        }
    }
}
