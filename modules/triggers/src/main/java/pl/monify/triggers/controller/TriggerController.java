package pl.monify.triggers.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.monify.triggers.dto.WebhookTriggerRequest;
import pl.monify.triggers.model.TriggerEntity;
import pl.monify.triggers.service.TriggerService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/triggers")
public class TriggerController {

    private static final Logger log = LoggerFactory.getLogger(TriggerController.class);
    private final TriggerService service;

    public TriggerController(TriggerService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TRIGGER_VIEW')")
    public List<TriggerEntity> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TRIGGER_VIEW')")
    public TriggerEntity getById(@PathVariable("id") String id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAuthority('TRIGGER_ADMIN')")
    @PostMapping
    public TriggerEntity create(@RequestBody TriggerEntity entity) {
        return service.save(entity);
    }

    @PreAuthorize("hasAuthority('TRIGGER_ADMIN')")
    @PutMapping("/{id}")
    public TriggerEntity update(@PathVariable("id") String id, @RequestBody TriggerEntity entity) {
        entity.setId(id);
        return service.save(entity);
    }

    @PreAuthorize("hasAuthority('TRIGGER_ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        service.delete(id);
    }

    @PostMapping("/webhook/{key}")
    public ResponseEntity<Void> fireWebhook(@PathVariable("key") String key, @RequestBody WebhookTriggerRequest request) {
        service.fireWebhook(key, request.getPayload());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/fire")
    @PreAuthorize("hasAuthority('TRIGGER_ADMIN')")
    public ResponseEntity<Void> fireFromUI(@PathVariable("id") String id, @RequestBody Map<String, Object> payload) {
        log.info("Trigger {} fired from UI", id);
        service.fireById(id, payload);
        return ResponseEntity.accepted().build();
    }
}
