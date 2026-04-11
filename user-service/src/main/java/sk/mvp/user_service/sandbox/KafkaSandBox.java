package sk.mvp.user_service.sandbox;

import org.slf4j.MDC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import sk.mvp.common.factory.UserEventFactory;
import sk.mvp.user_service.async.producer.IEventProducer;

import java.util.UUID;

//@Component
public class KafkaSandBox implements CommandLineRunner {
    private IEventProducer kafkaEventProducer;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserEventFactory userEventFactory;

    public KafkaSandBox(IEventProducer kafkaEventProducer, KafkaTemplate<String, String> kafkaTemplate, UserEventFactory userEventFactory) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.kafkaTemplate = kafkaTemplate;
        this.userEventFactory = userEventFactory;
    }

    @Override
    public void run(String... args) throws Exception {
        String topic = "user-event-topic";
        String topic2 = "test-topic";
        int counter = 0;

        ;


        while (true) {
//            UserRegisteredEvent event = new UserRegisteredEvent("ferino@gmail.com", "123");
//            PasswordChangeRequestedEvent event1 = new PasswordChangeRequestedEvent("555");
          //  MDC.put("X-Correlation-Id", UUID.randomUUID().toString());

            counter++;

//            BaseEvent<UserRegisteredPayload> userRegisteredEvent = userEventFactory.
//                    createUserRegisteredEvent("feroo@gmail.com", "depp link", counter+"");
//            BaseEvent<PasswordResetPayload> passwordChangeRequestedEvent = userEventFactory.
//                    createPasswordChangeRequestedEvent("janoo@gmail.com", "link password", counter+"");
//
//            kafkaEventProducer.produce(topic, userRegisteredEvent);
//            kafkaEventProducer.produce(topic, passwordChangeRequestedEvent);
            //kafkaTemplate.send(topic2,"Ahoj");
            //System.out.println("send");

            System.out.println(kafkaEventProducer.isBrokerUp());
            Thread.sleep(5000); // 5 sekúnd
        }

    }
}
