package pl.monify.agentsregistry.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.monify.agentsregistry.messaging.AgentRegisteredMessage;
import pl.monify.agentsregistry.model.RegisteredActionInstance;
import pl.monify.agentsregistry.service.ActionRegistryService;

import java.time.Instant;
import java.util.Optional;

@Component
public class AgentRegisteredListener {

    private static final Logger log = LoggerFactory.getLogger(AgentRegisteredListener.class);
    private final ActionRegistryService registryService;
    private static final String TOPIC = "agent.registration.event";

    public AgentRegisteredListener(ActionRegistryService registryService) {
        this.registryService = registryService;
    }

    @KafkaListener(
            topics = TOPIC,
            groupId = "agent.registry.consumer.group",
            containerFactory = "kafkaAgentRegisteredMessageListenerContainerFactory"
    )
    public void handle(AgentRegisteredMessage message) {
        var instance = new RegisteredActionInstance();
        Optional<RegisteredActionInstance> registeredAgentById = registryService.findByAgentId(message.agentId());
        log.info("Agent {} registered with name {}", message.agentId(), message.action());
        if (registeredAgentById.isPresent()) {
            log.info("Agent {} already registered - updating", message.agentId());
            instance = registeredAgentById.get();
        }

        instance.setName(message.action());
        instance.setAgentId(message.agentId());
        instance.setDisplayName(message.displayName());
        instance.setInputSchema(message.inputSchema());
        instance.setOutputSchema(message.outputSchema());
        instance.setActive(true);
        instance.setRegisteredAt(Instant.now());
        instance.setLastHealthCheck(Instant.now());

        registryService.register(instance);
    }
}
