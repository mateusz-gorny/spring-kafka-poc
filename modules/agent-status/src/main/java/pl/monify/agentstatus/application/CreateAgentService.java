package pl.monify.agentstatus.application;

import pl.monify.agentstatus.adapter.mongo.AgentSecretDocument;
import pl.monify.agentstatus.adapter.mongo.AgentSecretRepository;
import pl.monify.agentstatus.domain.event.AgentCreatedEvent;
import pl.monify.agentstatus.domain.port.out.AgentCreatedEventPublisherPort;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

public class CreateAgentService {

    private final AgentSecretRepository repository;
    private final AgentCreatedEventPublisherPort eventPublisher;

    public CreateAgentService(AgentSecretRepository repository, AgentCreatedEventPublisherPort eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public AgentSecretDocument create(String name) {
        String id = UUID.randomUUID().toString();
        String secret = generateSecret();
        AgentSecretDocument doc = new AgentSecretDocument(id, name, secret);
        repository.save(doc);
        eventPublisher.publish(new AgentCreatedEvent(id, secret, name));
        return doc;
    }

    private String generateSecret() {
        byte[] bytes = new byte[48];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
