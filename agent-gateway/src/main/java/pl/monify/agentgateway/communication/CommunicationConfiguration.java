package pl.monify.agentgateway.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import pl.monify.agentgateway.communication.application.HandleActionExecutionResultService;
import pl.monify.agentgateway.communication.application.PingAgentService;
import pl.monify.agentgateway.communication.application.RegisterAgentService;
import pl.monify.agentgateway.communication.domain.port.out.AgentPingReceivedEventPublisherPort;
import pl.monify.agentgateway.token.config.JwtKeysProperties;
import pl.monify.agentgateway.communication.domain.port.in.HandleActionExecutionResultUseCase;
import pl.monify.agentgateway.communication.domain.port.in.PingAgentUseCase;
import pl.monify.agentgateway.communication.domain.port.in.RegisterAgentUseCase;
import pl.monify.agentgateway.communication.domain.port.out.ActionRegistryPort;
import pl.monify.agentgateway.communication.domain.port.out.ActionResultSenderPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort;
import pl.monify.agentgateway.communication.domain.port.out.RegisterAgentSessionPort;
import pl.monify.agentgateway.communication.domain.port.out.UnregisterAgentSessionPort;
import pl.monify.agentgateway.communication.web.AgentMessageDispatcher;
import pl.monify.agentgateway.communication.web.AgentMessageHandler;
import pl.monify.agentgateway.communication.web.AgentWebSocketHandler;
import pl.monify.agentgateway.communication.web.handler.ExecutionResultHandler;
import pl.monify.agentgateway.communication.web.handler.PingHandler;
import pl.monify.agentgateway.communication.web.handler.RegisterHandler;
import pl.monify.agentgateway.token.domain.port.in.JwtTokenParserPort;

import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({
        JwtKeysProperties.class
})
public class CommunicationConfiguration {
    @Bean
    public HandlerMapping webSocketMapping(AgentWebSocketHandler handler) {
        var map = Map.of("/ws/agent", handler);
        var mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(-1);
        return mapping;
    }

    @Bean
    public AgentMessageDispatcher agentMessageDispatcher(ObjectMapper objectMapper, List<AgentMessageHandler> handlers) {
        return new AgentMessageDispatcher(objectMapper, handlers);
    }

    @Bean
    public AgentWebSocketHandler agentWebSocketHandler(
            JwtTokenParserPort jwtTokenParser,
            RegisterAgentSessionPort register,
            UnregisterAgentSessionPort unregister,
            AgentMessageDispatcher dispatcher
    ) {
        return new AgentWebSocketHandler(jwtTokenParser, register, unregister, dispatcher);
    }

    @Bean
    public PingAgentUseCase pingAgentUseCase(AgentPingReceivedEventPublisherPort eventPublisher) {
        return new PingAgentService(eventPublisher);
    }

    @Bean
    public RegisterAgentUseCase registerAgentUseCase(
            ActionRegistryPort actionRegistry,
            AgentRegisteredEventSenderPort sender
    ) {
        return new RegisterAgentService(actionRegistry, sender);
    }

    @Bean
    public HandleActionExecutionResultUseCase resultHandlerUseCase(ActionResultSenderPort sender) {
        return new HandleActionExecutionResultService(sender);
    }

    @Bean
    public PingHandler pingHandler(PingAgentUseCase pingAgentUseCase, ObjectMapper objectMapper) {
        return new PingHandler(pingAgentUseCase, objectMapper);
    }

    @Bean
    public RegisterHandler registerHandler(ObjectMapper objectMapper, RegisterAgentUseCase useCase) {
        return new RegisterHandler(objectMapper, useCase);
    }

    @Bean
    public ExecutionResultHandler executionResultHandler(ObjectMapper objectMapper, HandleActionExecutionResultUseCase useCase) {
        return new ExecutionResultHandler(objectMapper, useCase);
    }
}
