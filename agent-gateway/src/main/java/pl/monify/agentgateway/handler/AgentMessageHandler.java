package pl.monify.agentgateway.handler;

import pl.monify.agentgateway.socket.AgentSession;
import reactor.core.publisher.Mono;

public interface AgentMessageHandler {
    String type();
    Mono<Void> handle(String json, AgentSession session) throws Exception;
}
