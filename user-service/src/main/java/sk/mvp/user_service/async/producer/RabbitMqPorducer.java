package sk.mvp.user_service.async.producer;

import sk.mvp.common.event.BaseEvent;

public class RabbitMqPorducer implements IEventProducer {
    @Override
    public void produce(String topic, BaseEvent<?> event) {
    }

    @Override
    public boolean isBrokerUp() {
        return false;
    }
}
