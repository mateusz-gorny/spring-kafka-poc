package pl.monify.agent.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import pl.monify.agent.registration.AgentRegistrationClient;
import pl.monify.agent.task.AgentTaskExecutor;

import java.util.List;
import java.util.Map;

public class WebSocketListenerImpl extends WebSocketListener {

    private final ObjectMapper mapper;
    private final SessionRegistry registry;
    private final AgentTaskExecutor executor;
    private final AgentRegistrationClient agentRegistrationClient;

    public WebSocketListenerImpl(ObjectMapper mapper,
                                 SessionRegistry registry,
                                 AgentTaskExecutor executor,
                                 AgentRegistrationClient agentRegistrationClient) {
        this.mapper = mapper;
        this.registry = registry;
        this.executor = executor;
        this.agentRegistrationClient = agentRegistrationClient;
    }

    @Override
    public void onOpen(WebSocket socket, Response response) {
        registry.register(socket);
        agentRegistrationClient.registerAll();
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        try {
            Map<?, ?> msg = mapper.readValue(text, Map.class);
            if ("ActionExecutionRequest".equals(msg.get("type"))) {
                executor.execute();

                var result = Map.of(
                        "type", "ActionExecutionResult",
                        "correlationId", msg.get("correlationId"),
                        "payload", Map.of(
                                "status", "SUCCESS",
                                "output", Map.of("result", "done"),
                                "logs", List.of("Step OK")
                        )
                );
                registry.get().send(mapper.writeValueAsString(result));
            }
        } catch (Exception ignored) {}
    }
}
