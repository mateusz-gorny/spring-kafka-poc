package pl.monify.agent.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agent.config.AgentProperties;

import java.util.Map;

public class TokenClient {

    private static final Logger log = LoggerFactory.getLogger(TokenClient.class);
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
            String body = mapper.writeValueAsString(Map.of(
                    "agentId", props.name(),
                    "secret", props.secret(),
                    "teamId", props.teamId(),
                    "actions", props.actions()
            ));

            Request request = new Request.Builder()
                    .url(props.authUrl())
                    .post(RequestBody.create(body, MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new RuntimeException("Bad token response: " + response.code());
                var json = mapper.readValue(response.body().string(), Map.class);
                return (String) json.get("token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token fetch failed", e);
        }
    }
}
