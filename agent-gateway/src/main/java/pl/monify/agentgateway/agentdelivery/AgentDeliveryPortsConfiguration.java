package pl.monify.agentgateway.agentdelivery;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.monify.agentgateway.agentdelivery.adapter.websocket.WebSocketAgentSenderAdapter;
import pl.monify.agentgateway.agentdelivery.domain.port.out.AgentSenderPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentSessionFinderPort;

@Configuration
public class AgentDeliveryPortsConfiguration {
    @Bean
    public AgentSenderPort agentSenderPort(ObjectMapper objectMapper, AgentSessionFinderPort agentSessionFinderPort) {
        return new WebSocketAgentSenderAdapter(objectMapper, agentSessionFinderPort);
    }
}
