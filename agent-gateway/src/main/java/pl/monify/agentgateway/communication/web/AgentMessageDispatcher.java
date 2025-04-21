package pl.monify.agentgateway.communication.web;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import reactor.core.publisher.Mono;

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

    public Mono<Void> dispatch(AgentSession session, String json) {
        log.info("[WS] Received message from agent {}", session.id());
        try {
            BaseAgentMessage base = objectMapper.readValue(json, BaseAgentMessage.class);
            AgentMessageHandler handler = handlers.get(base.type());
            log.info("[WS] Dispatching message of type {}", base.type());

            if (handler == null) {
                log.error("[WS] Unknown message type: {}", base.type());
                return session.sendText("{\"type\":\"error\",\"message\":\"unknown type\"}");
            }

            return handler.handle(json, session);
        } catch (JsonParseException e) {
            log.error("[WS] Failed to parse incoming message", e);
            return session.sendText("{\"type\":\"error\",\"message\":\"Failed to parse incoming message, check JSON format\"}");
        } catch (Exception e) {
            log.error("[WS] Failed to process request", e);
            return session.sendText("{\"type\":\"error\",\"message\":\"Bad request\"}");
        }
    }
}
