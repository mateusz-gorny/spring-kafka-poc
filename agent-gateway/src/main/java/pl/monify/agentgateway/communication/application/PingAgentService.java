package pl.monify.agentgateway.communication.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.PingAgentUseCase;

public class PingAgentService implements PingAgentUseCase {

    private static final Logger log = LoggerFactory.getLogger(PingAgentService.class);

    @Override
    public void pong(AgentSession session) {
        log.debug("[WS] Sending pong to agent {}", session.id());
        session.sendText("{\"type\":\"pong\"}").subscribe();
    }
}
