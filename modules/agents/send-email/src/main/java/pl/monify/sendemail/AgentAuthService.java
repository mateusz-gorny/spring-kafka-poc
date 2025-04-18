package pl.monify.sendemail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AgentAuthService {

    private static final Logger log = LoggerFactory.getLogger(AgentAuthService.class);

    private final AgentProperties properties;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AgentAuthService(AgentProperties properties, OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public String fetchToken() {
        try {
            String url = properties.getAuthUrl();
            String json = objectMapper.writeValueAsString(Map.of(
                    "agentId", properties.getName(),
                    "secret", properties.getAuthSecret()
            ));

            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Auth request failed with code: " + response.code());
                }

                String responseBody = response.body().string();
                Map<String, Object> result = objectMapper.readValue(responseBody, new TypeReference<>() {});
                String token = (String) result.get("token");

                if (token == null || token.isBlank()) {
                    log.error("[AGENT] JWT token not present in response");
                    throw new RuntimeException("Token not present in response");
                }

                return token;
            }
        } catch (Exception e) {
            log.error("[AGENT] Failed to fetch JWT from auth server", e);
            throw new RuntimeException("Token fetch failed", e);
        }
    }
}
