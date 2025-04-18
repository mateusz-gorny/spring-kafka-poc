package pl.monify.agentgateway.testutil

import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import pl.monify.agentgateway.socket.AgentSession

class MockAgentSession extends AgentSession {

    List<String> sentMessages = []

    MockAgentSession(String action, String teamId, WebSocketSession session) {
        super(action, teamId, session)
    }

    @Override
    Mono<Void> sendText(String message) {
        sentMessages << message
        return Mono.empty()
    }
}
