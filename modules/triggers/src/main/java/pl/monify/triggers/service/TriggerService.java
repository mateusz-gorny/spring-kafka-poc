package pl.monify.triggers.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.monify.triggers.model.TriggerEntity;
import pl.monify.triggers.publisher.TriggerEventPublisher;
import pl.monify.triggers.repository.TriggerRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TriggerService {

    private static final Logger log = LoggerFactory.getLogger(TriggerService.class);
    private final TriggerRepository repository;
    private final TriggerEventPublisher publisher;

    public TriggerService(TriggerRepository repository, TriggerEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public List<TriggerEntity> getAll() {
        return repository.findAll();
    }

    public TriggerEntity getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trigger not found"));
    }

    public TriggerEntity save(TriggerEntity trigger) {
        trigger.setKey(UUID.randomUUID().toString());
        return repository.save(trigger);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public void fireWebhook(String key, Map<String, Object> body) {
        TriggerEntity trigger = repository.findByKey(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trigger not found"));

        fire(trigger, "webhook", body);
    }

    public void fireById(String id, Map<String, Object> body) {
        TriggerEntity trigger = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trigger not found"));

        fire(trigger, "manual", body);
    }

    private void fire(TriggerEntity trigger, String source, Map<String, Object> body) {
        Map<String, Object> event = Map.of(
                "type", "workflow_trigger",
                "workflowIds", trigger.getWorkflowIds(),
                "payload", Map.of(
                        "from", source,
                        "data", body
                )
        );

        log.info("Trigger {} fired with event {}", trigger.getId(), event);

        publisher.publish(event);
    }
}
