package pl.monify.agentgateway.agentdelivery.adapter.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;
import pl.monify.agentgateway.agentdelivery.domain.port.out.AgentSenderPort;
import pl.monify.agentgateway.communication.adapter.registry.RegisteredAction;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.domain.port.out.AgentSessionFinderPort;
import reactor.core.publisher.Mono;

import java.util.Map;

public class WebSocketAgentSenderAdapter implements AgentSenderPort {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAgentSenderAdapter.class);
    private final ObjectMapper objectMapper;
    private final AgentSessionFinderPort agentSessionFinderPort;

    public WebSocketAgentSenderAdapter(ObjectMapper objectMapper, AgentSessionFinderPort agentSessionFinderPort) {
        this.objectMapper = objectMapper;
        this.agentSessionFinderPort = agentSessionFinderPort;
    }

    @Override
    public void send(ActionExecutionRequestMessage request) {
        log.info("[WS] Sending action execution request to agent {}", request.teamId());
        var action = agentSessionFinderPort.find(request.teamId(), request.action());
        if (action.isPresent()) {
            try {
                RegisteredAction registeredAction = action.get();
                AgentSession registeredSession = registeredAction.session();

                MDC.put("workflowInstanceId", request.workflowInstanceId());
                MDC.put("correlationId", request.correlationId());
                MDC.put("sessionId", registeredSession.id());
                MDC.put("teamId", request.teamId());

                log.info("[WS] Sending to {}: {}", registeredSession.id(), request.toString());

                String json = objectMapper.writeValueAsString(Map.of(
                        "type", "ActionExecutionRequest",
                        "correlationId", request.correlationId(),
                        "input", request.input()
                ));
                log.debug("[WS] Sending to {}: {}", registeredSession.id(), json);
                registeredSession.session().send(Mono.just(registeredSession.session().textMessage(json))).subscribe();
            } catch (Exception error) {
                log.error("[WS] Failed sending message to agent", error);
            } finally {
                MDC.clear();
            }
        }
    }
}
