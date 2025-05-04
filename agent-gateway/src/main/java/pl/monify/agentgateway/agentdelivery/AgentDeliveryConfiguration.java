package pl.monify.agentgateway.agentdelivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.monify.agentgateway.agentdelivery.adapter.mongo.ActionExecutionRequestMongoRepository;
import pl.monify.agentgateway.agentdelivery.domain.application.AgentDispatcher;
import pl.monify.agentgateway.agentdelivery.domain.port.out.AgentSenderPort;
import pl.monify.agentgateway.agentdelivery.messaging.ActionExecutionKafkaListener;
import pl.monify.agentgateway.communication.domain.port.out.AgentSessionFinderPort;

@Configuration
public class AgentDeliveryConfiguration {
    @Bean
    public ActionExecutionKafkaListener actionExecutionKafkaListener(
            AgentDispatcher dispatcher,
            ActionExecutionRequestMongoRepository repository,
            ObjectMapper mapper
    ) {
        return new ActionExecutionKafkaListener(dispatcher, repository, mapper);
    }

    @Bean
    public AgentDispatcher agentDispatcher(AgentSenderPort sender, AgentSessionFinderPort agentSessionFinderPort) {
        return new AgentDispatcher(sender, agentSessionFinderPort);
    }
}
