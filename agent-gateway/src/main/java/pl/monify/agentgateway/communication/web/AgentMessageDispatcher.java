package pl.monify.agentgateway.communication.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.communication.domain.model.AgentSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentMessageDispatcher {

    private static final Logger log = LoggerFactory.getLogger(AgentMessageDispatcher.class);

    private final ObjectMapper objectMapper;
    private final Map<String, AgentMessageHandler> handlers = new HashMap<>();

    public AgentMessageDispatcher(ObjectMapper objectMapper, List<AgentMessageHandler> handlerList) {
        this.objectMapper = objectMapper;
        for (AgentMessageHandler handler : handlerList) {
            handlers.put(handler.type(), handler);
        }
    }

    public reactor.core.publisher.Mono<Void> dispatch(AgentSession session, String json) {
        try {
            BaseAgentMessage base = objectMapper.readValue(json, BaseAgentMessage.class);
            AgentMessageHandler handler = handlers.get(base.type());

            if (handler == null) {
                log.warn("[WS] Unknown message type: {}", base.type());
                return session.sendText("{\"type\":\"error\",\"message\":\"unknown type\"}");
            }

            return handler.handle(json, session);
        } catch (Exception e) {
            log.error("[WS] Failed to parse incoming message", e);
            return session.sendText("{\"type\":\"error\",\"message\":\"bad request\"}");
        }
    }
}
