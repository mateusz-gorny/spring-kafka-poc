package pl.monify.agent.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.monify.agent.config.AgentProperties;
import pl.monify.agent.task.ActionTaskExecutor;

import java.util.Arrays;
import java.util.Map;

public class TokenClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final AgentProperties props;
    private final ActionTaskExecutor[] actionTaskExecutors;

    public TokenClient(OkHttpClient client, ObjectMapper mapper, AgentProperties props, ActionTaskExecutor[] actionTaskExecutors) {
        this.client = client;
        this.mapper = mapper;
        this.props = props;
        this.actionTaskExecutors = actionTaskExecutors;
    }

    public String fetchToken() {
        try {
            Map<String, Object> body = Map.of(
                    "agentId", props.name(),
                    "secret", props.secret(),
                    "teamId", props.teamId(),
                    "actions", Arrays.stream(actionTaskExecutors)
                            .map(ActionTaskExecutor::getActionName)
                            .toList()
            );

            Request request = new Request.Builder()
                    .url(props.authUrl())
                    .post(RequestBody.create(mapper.writeValueAsString(body), MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Auth failed: " + response.code());
                Map<?, ?> json = mapper.readValue(response.body().string(), Map.class);
                return (String) json.get("token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token error", e);
        }
    }
}
