package pl.monify.agent.gateway;

import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocketListener;
import pl.monify.agent.auth.TokenClient;
import pl.monify.agent.config.AgentProperties;

public class GatewayClient {

    private final AgentProperties props;
    private final OkHttpClient client;
    private final TokenClient tokenClient;
    private final WebSocketListener listener;

    public GatewayClient(AgentProperties props,
                         OkHttpClient client,
                         TokenClient tokenClient,
                         WebSocketListener listener) {
        this.props = props;
        this.client = client;
        this.tokenClient = tokenClient;
        this.listener = listener;
    }

    @PostConstruct
    public void connect() {
        try {
            String token = tokenClient.fetchToken();
            Request request = new Request.Builder()
                    .url(props.gatewayUrl())
                    .header("Authorization", "Bearer " + token)
                    .build();

            client.newWebSocket(request, listener);
        } catch (Exception e) {
            throw new RuntimeException("Agent failed to connect to gateway", e);
        }
    }
}
