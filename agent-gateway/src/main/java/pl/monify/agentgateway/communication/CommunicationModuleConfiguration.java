package pl.monify.agentgateway.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.agentgateway.communication.adapter.messaging.*;
import pl.monify.agentgateway.communication.adapter.ratelimit.AgentRateLimiter;
import pl.monify.agentgateway.communication.adapter.ratelimit.RateLimiterServiceAdapter;
import pl.monify.agentgateway.communication.adapter.token.*;
import pl.monify.agentgateway.communication.application.*;
import pl.monify.agentgateway.communication.domain.port.in.*;
import pl.monify.agentgateway.communication.domain.port.out.*;
import pl.monify.agentgateway.communication.web.*;
import pl.monify.agentgateway.communication.web.handler.*;

import java.util.List;
import java.util.Map;

@Configuration
public class CommunicationModuleConfiguration {
    @Bean
    public JwtTokenKeyProviderPort jwtTokenKeyProvider(@Value("#{${monify.jwt.keys}}") Map<String, String> keys) {
        return new InMemoryJwtKeyProvider(keys);
    }

    @Bean
    public JwtKeyLocator jwtKeyLocator(JwtTokenKeyProviderPort provider) {
        return new JwtKeyLocator(provider);
    }

    @Bean
    public JwtParser jwtParser(JwtKeyLocator locator) {
        return io.jsonwebtoken.Jwts.parser()
                .keyLocator(locator)
                .build();
    }

    @Bean
    public JwtTokenParserPort jwtTokenParser(JwtParser parser) {
        return new JwtTokenParserAdapter(parser);
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
            ConnectionRateLimiterPort connectionLimiter,
            AgentMessageDispatcher dispatcher
    ) {
        return new AgentWebSocketHandler(jwtTokenParser, register, unregister, connectionLimiter, dispatcher);
    }

    @Bean
    public AgentRateLimiter agentRateLimiter() {
        return new AgentRateLimiter(100, 5);
    }

    @Bean
    public RateLimiterServiceAdapter rateLimiterServiceAdapter(AgentRateLimiter limiter) {
        return new RateLimiterServiceAdapter(limiter);
    }

    @Bean
    public PingAgentUseCase pingAgentUseCase() {
        return new PingAgentService();
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
    public AgentRegisteredEventSenderPort agentRegisteredSender(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.agent-registration-topic}") String topic
    ) {
        return new AgentRegisteredKafkaSender(kafkaTemplate, topic);
    }

    @Bean
    public ActionResultSenderPort actionResultSender(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.action-result-topic}") String topic
    ) {
        return new ActionResultKafkaSender(kafkaTemplate, topic);
    }

    @Bean
    public PingHandler pingHandler(MessageRateLimiterPort limiter, PingAgentUseCase useCase) {
        return new PingHandler(limiter, useCase);
    }

    @Bean
    public RegisterHandler registerHandler(ObjectMapper objectMapper, MessageRateLimiterPort limiter, RegisterAgentUseCase useCase) {
        return new RegisterHandler(objectMapper, limiter, useCase);
    }

    @Bean
    public ExecutionResultHandler executionResultHandler(ObjectMapper objectMapper, MessageRateLimiterPort limiter, HandleActionExecutionResultUseCase useCase) {
        return new ExecutionResultHandler(objectMapper, limiter, useCase);
    }
}
