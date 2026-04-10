package sk.mvp.user_service.async.producer;

import sk.mvp.common.event.BaseEvent;

public interface IEventProducer {
    void produce(String topic, BaseEvent<?> event);
}
