package pl.monify.agent.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agent.task.TaskExecutor;

import java.util.List;
import java.util.Map;

public class WebSocketListenerImpl extends WebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketListenerImpl.class);

    private final GatewayClient gateway;
    private final ObjectMapper mapper;
    private final TaskExecutor executor;

    public WebSocketListenerImpl(GatewayClient gateway, ObjectMapper mapper, TaskExecutor executor) {
        this.gateway = gateway;
        this.mapper = mapper;
        this.executor = executor;
    }

    @Override
    public void onOpen(WebSocket ws, Response response) {
        log.info("[AGENT] Connected to gateway.");
    }

    @Override
    public void onMessage(WebSocket ws, String text) {
        try {
            Map<String, Object> msg = mapper.readValue(text, Map.class);
            if ("ActionExecutionRequest".equals(msg.get("type"))) {
                log.info("[AGENT] Received execution request");

                String correlationId = (String) msg.get("correlationId");
                executor.execute();

                var result = Map.of(
                        "type", "ActionExecutionResult",
                        "correlationId", correlationId,
                        "payload", Map.of(
                                "status", "SUCCESS",
                                "output", Map.of("message", "Action completed"),
                                "logs", List.of("Execution done")
                        )
                );
                gateway.send(mapper.writeValueAsString(result));
                log.info("[AGENT] Result sent");
            }
        } catch (Exception e) {
            log.error("[AGENT] Message handling error", e);
        }
    }

    @Override
    public void onFailure(WebSocket ws, Throwable t, Response r) {
        log.error("[AGENT] WebSocket error", t);
    }
}
