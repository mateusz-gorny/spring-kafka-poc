package pl.monify.agent.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agent.model.ActionExecutionRequestMessageModel;
import pl.monify.agent.model.ActionExecutionResultModel;
import pl.monify.agent.model.ExecutorResultModel;
import pl.monify.agent.model.RequestActionExecutionMessage;
import pl.monify.agent.registration.AgentRegistrationClient;
import pl.monify.agent.task.ActionTaskExecutor;
import pl.monify.agent.model.ActionType;
import pl.monify.agent.ws.model.BaseMessageModel;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class WebSocketListenerImpl extends WebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketListenerImpl.class);

    private final ObjectMapper mapper;
    private final SessionRegistry registry;
    private final ActionTaskExecutor[] actionTaskExecutors;
    private final AgentRegistrationClient agentRegistrationClient;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public WebSocketListenerImpl(
            ObjectMapper mapper,
            SessionRegistry registry,
            ActionTaskExecutor[] actionTaskExecutors,
            AgentRegistrationClient agentRegistrationClient
    ) {
        this.mapper = mapper;
        this.registry = registry;
        this.actionTaskExecutors = actionTaskExecutors;
        this.agentRegistrationClient = agentRegistrationClient;
    }

    @Override
    public void onOpen(WebSocket socket, Response response) {
        registry.register(socket);
        agentRegistrationClient.registerAll();
    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        log.info("[WS] Received raw: {}", text);
        try {
            BaseMessageModel base = mapper.readValue(text, BaseMessageModel.class);
            switch (base.type()) {
                case "ActionExecutionRequest" -> {
                    ActionExecutionRequestMessageModel message =
                            mapper.readValue(text, ActionExecutionRequestMessageModel.class);

                    Optional<ActionTaskExecutor> executorOpt = Arrays.stream(actionTaskExecutors)
                            .filter(e -> e.getActionName().equals(message.action()))
                            .findFirst();

                    if (executorOpt.isEmpty()) {
                        log.error("[WS] No executor found for action {}", message.action());
                        registry.get().send(mapper.writeValueAsString(
                                new ActionExecutionResultModel(message.correlationId(), new ExecutorResultModel("false", Map.of(), List.of()))
                        ));
                        return;
                    }

                    ActionTaskExecutor executor = executorOpt.get();
                    ExecutorResultModel result = executor.execute(message);
                    registry.get().send(mapper.writeValueAsString(
                            new ActionExecutionResultModel(message.correlationId(), result)
                    ));

                    log.info("[WS] Action {} executed", message.action());

                    if (executor.getActionType() == ActionType.TRIGGER) {
                        log.info("[WS] Resending request-task for trigger action: {}", message.action());
                        Duration delay = executor.getTtl();
                        scheduler.schedule(() -> {
                            try {
                                var request = new RequestActionExecutionMessage(message.action(), delay.toString());
                                var json = mapper.writeValueAsString(request);
                                registry.get().send(json);
                                log.info("[WS] Resent request-task for trigger action: {}", message.action());
                            } catch (Exception e) {
                                log.error("[WS] Failed to resend request-task", e);
                            }
                        }, delay.toSeconds(), TimeUnit.SECONDS);
                    }
                }

                case "error" -> log.error("[WS] Received error message: {}", text);

                default -> log.info("[WS] Ignoring unsupported message type: {}", base.type());
            }
        } catch (Exception e) {
            log.error("[WS] Failed to process message: {}", text, e);
        }
    }
}
