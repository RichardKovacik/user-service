package sk.mvp.user_service.async.outbox.service;

import sk.mvp.common.event.BaseEvent;
import sk.mvp.user_service.async.outbox.dto.OutboxDTO;
import sk.mvp.user_service.async.outbox.entity.OutboxEvent;
import sk.mvp.user_service.common.exception.OutboxNotFoundException;

import java.util.UUID;

public interface IOutBoxService {
    <T>void saveOutbox(BaseEvent<T> event);
    <T> BaseEvent<T> findOutboxById(UUID id) throws OutboxNotFoundException;
    int markAsProcessed(UUID id);


}
