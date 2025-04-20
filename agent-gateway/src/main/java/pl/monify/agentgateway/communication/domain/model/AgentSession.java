package pl.monify.agentgateway.communication.domain.model;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public record AgentSession(
        WebSocketSession session,
        String teamId,
        String action
) {
    public String id() {
        return session.getId();
    }

    public Mono<Void> sendText(String message) {
        return session.send(Mono.just(session.textMessage(message)));
    }
}
