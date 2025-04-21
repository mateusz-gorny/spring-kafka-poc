package pl.monify.agent.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agent.auth.TokenClient;
import pl.monify.agent.config.AgentProperties;
import pl.monify.agent.task.TaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GatewayClient {

    private static final Logger log = LoggerFactory.getLogger(GatewayClient.class);

    private final AgentProperties props;
    private final TokenClient tokenClient;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final TaskExecutor executor;
    private WebSocket socket;
    private final ScheduledExecutorService ping = Executors.newSingleThreadScheduledExecutor();

    public GatewayClient(AgentProperties props,
                         TokenClient tokenClient,
                         OkHttpClient client,
                         ObjectMapper mapper,
                         TaskExecutor executor) {
        this.props = props;
        this.tokenClient = tokenClient;
        this.client = client;
        this.mapper = mapper;
        this.executor = executor;
    }

    public void connect() {
        try {
            String token = tokenClient.fetchToken();
            Request req = new Request.Builder()
                    .url(props.gatewayUrl() + "?token=" + token)
                    .build();

            this.socket = client.newWebSocket(req, new WebSocketListenerImpl(this, mapper, executor));
            startPinging();
        } catch (Exception e) {
            log.error("[AGENT] Connection failed", e);
        }
    }

    public void send(String json) {
        if (socket != null) socket.send(json);
    }

    private void startPinging() {
        ping.scheduleAtFixedRate(() -> send("{\"type\":\"ping\"}"), 10, 10, TimeUnit.SECONDS);
    }
}
