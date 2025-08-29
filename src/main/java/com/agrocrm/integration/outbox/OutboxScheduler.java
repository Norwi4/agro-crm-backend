package com.agrocrm.integration.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class OutboxScheduler {
    private static final Logger log = LoggerFactory.getLogger(OutboxScheduler.class);
    
    private final OutboxRepository repo;

    public OutboxScheduler(OutboxRepository repo) { this.repo = repo; }

    @Scheduled(fixedDelay = 30000)
    public void pump() {
        try {
            List<Map<String,Object>> batch = repo.pullBatch(50);
            log.debug("Processing {} outbox events", batch.size());
            
            for (Map<String,Object> e : batch) {
                Long id = ((Number)e.get("id")).longValue();
                try {
                    // TODO: отправить в 1С/шину/внешний API
                    log.info("Sending outbox {} type={} payload={}", id, e.get("event_type"), e.get("payload"));
                    repo.markSent(id);
                } catch (Exception ex) {
                    log.error("Failed to process outbox event: id={}, type={}", id, e.get("event_type"), ex);
                    repo.markFailed(id, ex.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to pump outbox events", e);
        }
    }
}
