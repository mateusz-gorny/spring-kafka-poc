package pl.monify.agentsregistry.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.monify.agentsregistry.messaging.AgentRegisteredMessage;
import pl.monify.agentsregistry.model.RegisteredActionInstance;
import pl.monify.agentsregistry.service.ActionRegistryService;

import java.time.Instant;

@Component
public class AgentRegisteredListener {

    private final ActionRegistryService registryService;
    private static final String TOPIC = "agent.registration.event";

    public AgentRegisteredListener(ActionRegistryService registryService) {
        this.registryService = registryService;
    }

    @KafkaListener(
            topics = TOPIC,
            groupId = "agent.registry.consumer.group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(AgentRegisteredMessage message) {
        var instance = new RegisteredActionInstance();
        instance.setName(message.name());
        instance.setDisplayName(message.displayName());
        instance.setInputSchema(message.inputSchema());
        instance.setOutputSchema(message.outputSchema());
        instance.setActive(true);
        instance.setRegisteredAt(Instant.now());
        instance.setLastHealthCheck(Instant.now());

        registryService.register(instance);
    }
}
