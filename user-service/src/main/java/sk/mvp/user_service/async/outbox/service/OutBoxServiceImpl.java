package sk.mvp.user_service.async.outbox.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sk.mvp.common.event.BaseEvent;
import sk.mvp.user_service.async.outbox.entity.OutBoxStatus;
import sk.mvp.user_service.async.outbox.entity.OutboxEvent;
import sk.mvp.user_service.async.outbox.factory.OutboxFactory;
import sk.mvp.user_service.async.outbox.repository.OutBoxRepository;
import sk.mvp.user_service.common.exception.OutboxNotFoundException;
import sk.mvp.user_service.common.exception.QApplicationException;
import sk.mvp.user_service.common.exception.data.ErrorType;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutBoxServiceImpl implements IOutBoxService {
    private OutBoxRepository outBoxRepository;
    private OutboxFactory outboxFactory;

    public OutBoxServiceImpl(OutBoxRepository outBoxRepository, OutboxFactory outboxFactory) {
        this.outBoxRepository = outBoxRepository;
        this.outboxFactory = outboxFactory;
    }

    @Override
    @Transactional
    public <T> void saveOutbox(BaseEvent<T> event) {
        OutboxEvent outboxEvent = outboxFactory.createPendingOutboxEvent(event);
        outBoxRepository.save(outboxEvent);

    }

    @Override
    public <T> BaseEvent<T> findOutboxById(UUID id) throws OutboxNotFoundException {
        OutboxEvent event = outBoxRepository.findById(id).orElseThrow(() -> new OutboxNotFoundException("Outbox event with ID " + id + " not found."));
        return outboxFactory.toBaseEvent(event);
    }

    //TODO: DTO obeject better approach not returning db object directly
//    @Override
//    public OutboxEvent findOutboxById(UUID id) {
//        return  outBoxRepository.findById(id).orElseThrow(() -> new OutboxNotFoundException("Outbox event with ID " + id + " not found."));
//    }

    @Override
    @Transactional
    public int markAsProcessed(UUID id) {
        return outBoxRepository.markAsProcessed(id, OutBoxStatus.PROCESSED, Instant.now());
    }
}
