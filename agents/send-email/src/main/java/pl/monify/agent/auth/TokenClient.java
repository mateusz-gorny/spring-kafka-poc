package pl.monify.agent.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import pl.monify.agent.config.AgentProperties;

import java.util.Map;

public class TokenClient {

    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final AgentProperties props;

    public TokenClient(OkHttpClient client, ObjectMapper mapper, AgentProperties props) {
        this.client = client;
        this.mapper = mapper;
        this.props = props;
    }

    public String fetchToken() {
        try {
            Map<String, Object> body = Map.of(
                    "agentId", props.name(),
                    "secret", props.secret(),
                    "teamId", props.teamId(),
                    "actions", props.actions()
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
