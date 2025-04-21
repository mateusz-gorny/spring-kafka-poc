package pl.monify.agent.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agent.config.AgentProperties;
import pl.monify.agent.ws.SessionRegistry;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AgentRegistrationClient {

    private static final Logger log = LoggerFactory.getLogger(AgentRegistrationClient.class);
    private final AgentProperties props;
    private final ObjectMapper mapper;
    private final SessionRegistry registry;

    public AgentRegistrationClient(AgentProperties props, ObjectMapper mapper, SessionRegistry registry) {
        this.props = props;
        this.mapper = mapper;
        this.registry = registry;
    }

    public void registerAll() {
        WebSocket socket = registry.get();

        List<String> actions = props.actions();
        for (String action : actions) {
            try {
                Map<String, Object> message = Map.of(
                        "type", "register",
                        "teamId", props.teamId(),
                        "action", action,
                        "correlationId", UUID.randomUUID().toString(),
                        "inputSchema", Map.of("type", "object", "properties", Map.of(
                                "to", Map.of("type", "string"),
                                "subject", Map.of("type", "string"),
                                "body", Map.of("type", "string")
                        )),
                        "outputSchema", Map.of("type", "object", "properties", Map.of(
                                "result", Map.of("type", "string"),
                                "status", Map.of("type", "string")
                        ))
                );

                String json = mapper.writeValueAsString(message);
                socket.send(json);
            } catch (Exception e) {
                log.error("[REGISTER] Failed to send registration for action: {}", action, e);
            }
        }
    }
}
