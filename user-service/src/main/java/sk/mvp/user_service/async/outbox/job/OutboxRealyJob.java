package sk.mvp.user_service.async.outbox.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.mvp.user_service.async.outbox.service.IOutBoxService;

//@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxRealyJob {
    private final IOutBoxService outboxService;

    @Scheduled(fixedDelay = 30000)
    public void runJob() {
        // Voláme servisnú metódu, ktorá celá beží v transakcii
        outboxService.processPendingOutboxEventsBatch(50);
    }
}
