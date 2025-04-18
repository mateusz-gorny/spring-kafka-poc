package pl.monify.sendemail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class SendEmailAgent {

    private static final Logger log = LoggerFactory.getLogger(SendEmailAgent.class);

    private final AgentProperties properties;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client;
    private final AgentAuthService authService;
    private final AtomicBoolean connected = new AtomicBoolean(false);

    public SendEmailAgent(
            AgentProperties properties,
            ObjectMapper objectMapper,
            OkHttpClient client,
            AgentAuthService authService
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.client = client;
        this.authService = authService;
    }

    @Scheduled(fixedDelay = 5000)
    public void tryConnect() {
        if (connected.get()) {
            return;
        }

        try {
            log.info("[AGENT] Attempting to connect to gateway...");
            String token = authService.fetchToken();
            String wsUrl = properties.getGatewayUrl() + "?token=" + token;

            Request request = new Request.Builder().url(wsUrl).build();
            client.newWebSocket(request, new WebSocketListener() {
                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    connected.set(true);
                    log.info("[AGENT] Connected to gateway.");
                    try {
                        String register = objectMapper.writeValueAsString(Map.of(
                                "type", "register",
                                "correlationId", "reg-" + properties.getName(),
                                "payload", Map.of(
                                        "action", properties.getName(),
                                        "inputSchema", Map.of("type", "object"),
                                        "outputSchema", Map.of("type", "object")
                                )
                        ));
                        webSocket.send(register);
                    } catch (Exception e) {
                        log.error("[AGENT] Failed to send register", e);
                    }
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    log.info("[AGENT] Received: {}", text);
                    try {
                        Map<String, Object> message = objectMapper.readValue(text, new TypeReference<>() {
                        });
                        if ("ping".equals(message.get("type"))) {
                            webSocket.send("{\"type\":\"pong\"}");
                            return;
                        }

                        if ("ActionExecutionRequest".equals(message.get("type"))) {
                            log.info("[AGENT] Processing ActionExecutionRequest: {}", text);
                            String correlationId = (String) message.get("correlationId");

                            Map<String, Object> result = Map.of(
                                    "type", "ActionExecutionResult",
                                    "correlationId", correlationId,
                                    "payload", Map.of(
                                            "status", "SUCCESS",
                                            "output", Map.of("info", "Email sent"),
                                            "logs", new String[]{"Email sent successfully"}
                                    )
                            );

                            webSocket.send(objectMapper.writeValueAsString(result));
                            log.info("[AGENT] Sent ActionExecutionResult.");
                        }
                    } catch (Exception e) {
                        log.error("[AGENT] Error while processing message", e);
                    }
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                    log.error("[AGENT] WebSocket failure", t);
                    connected.set(false);
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    log.warn("[AGENT] WebSocket closing: {} {}", code, reason);
                    connected.set(false);
                }

                @Override
                public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    log.warn("[AGENT] WebSocket closed: {} {}", code, reason);
                    connected.set(false);
                }
            });
        } catch (Exception e) {
            log.error("[AGENT] Connection error", e);
            connected.set(false);
        }
    }
}
