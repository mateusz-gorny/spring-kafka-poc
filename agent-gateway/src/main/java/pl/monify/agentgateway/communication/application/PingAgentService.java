package pl.monify.agentgateway.communication.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.communication.domain.event.AgentPingReceivedEvent;
import pl.monify.agentgateway.communication.domain.model.AgentHostStats;
import pl.monify.agentgateway.communication.domain.model.AgentPing;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.in.PingAgentUseCase;
import pl.monify.agentgateway.communication.domain.port.out.AgentPingReceivedEventPublisherPort;

import java.time.Instant;

public class PingAgentService implements PingAgentUseCase {

    private static final Logger log = LoggerFactory.getLogger(PingAgentService.class);
    private final AgentPingReceivedEventPublisherPort agentPingReceivedEventPublisherPort;

    public PingAgentService(AgentPingReceivedEventPublisherPort agentPingReceivedEventPublisherPort) {
        this.agentPingReceivedEventPublisherPort = agentPingReceivedEventPublisherPort;
    }

    @Override
    public void pong(AgentSession agentSession, AgentPing agentPing) {
        log.debug("[WS] Sending pong to agent {}", agentSession.id());
        agentSession.sendText("{\"type\":\"pong\"}").subscribe();
        agentPingReceivedEventPublisherPort.publish(new AgentPingReceivedEvent(
                agentPing.agentId(),
                agentSession.id(),
                agentPing.teamId(),
                agentPing.timestamp() != null ? agentPing.timestamp() : Instant.now(),
                new AgentHostStats(agentPing.hostStats())
        ));
    }
}
