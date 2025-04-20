package pl.monify.agentgateway.communication.web;

import pl.monify.agentgateway.communication.domain.model.AgentSession;
import reactor.core.publisher.Mono;

public interface AgentMessageHandler {
    String type();
    Mono<Void> handle(String json, AgentSession session) throws Exception;
}
