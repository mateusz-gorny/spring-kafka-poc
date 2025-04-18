package pl.monify.agentgateway.handler;

import org.springframework.stereotype.Component;
import pl.monify.agentgateway.socket.AgentSession;
import reactor.core.publisher.Mono;

@Component
public class PingMessageHandler implements AgentMessageHandler {

    @Override
    public String type() {
        return "ping";
    }

    @Override
    public Mono<Void> handle(String json, AgentSession session) {
        return session.sendText("{\"type\":\"pong\"}");
    }
}
