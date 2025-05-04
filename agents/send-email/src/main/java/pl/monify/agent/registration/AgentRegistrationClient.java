package pl.monify.agent.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agent.config.AgentProperties;
import pl.monify.agent.task.ActionTaskExecutor;
import pl.monify.agent.ws.SessionRegistry;

import java.util.Map;
import java.util.UUID;

public final class AgentRegistrationClient {

    private static final Logger log = LoggerFactory.getLogger(AgentRegistrationClient.class);
    private final AgentProperties props;
    private final ObjectMapper mapper;
    private final SessionRegistry registry;
    private final ActionTaskExecutor[] actionTaskExecutors;

    public AgentRegistrationClient(
            AgentProperties props,
            ObjectMapper mapper,
            SessionRegistry registry,
            ActionTaskExecutor[] actionTaskExecutors
    ) {
        this.props = props;
        this.mapper = mapper;
        this.registry = registry;
        this.actionTaskExecutors = actionTaskExecutors;
    }

    public void registerAll() {
        WebSocket socket = registry.get();

        for (ActionTaskExecutor actionTaskExecutor : actionTaskExecutors) {
            try {
                String json = mapper.writeValueAsString(Map.of(
                        "type", "register",
                        "teamId", props.teamId(),
                        "action", actionTaskExecutor.getActionName(),
                        "agentId", props.id(),
                        "ttl", actionTaskExecutor.getTtl().toSeconds(),
                        "correlationId", UUID.randomUUID().toString(),
                        "inputSchema", actionTaskExecutor.getInputSchema(),
                        "outputSchema", actionTaskExecutor.getOutputSchema()
                ));
                socket.send(json);
            } catch (Exception e) {
                log.error("[REGISTER] Failed to send registration for action: {}", actionTaskExecutor.getActionName(), e);
            }
        }
    }
}
