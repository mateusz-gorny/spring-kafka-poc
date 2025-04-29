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
import pl.monify.agent.registration.AgentRegistrationClient;
import pl.monify.agent.task.ActionTaskExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WebSocketListenerImpl extends WebSocketListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketListenerImpl.class);
    private final ObjectMapper mapper;
    private final SessionRegistry registry;
    private final ActionTaskExecutor[] actionTaskExecutors;
    private final AgentRegistrationClient agentRegistrationClient;

    public WebSocketListenerImpl(ObjectMapper mapper,
                                 SessionRegistry registry,
                                 ActionTaskExecutor[] actionTaskExecutors,
                                 AgentRegistrationClient agentRegistrationClient) {
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
            ActionExecutionRequestMessageModel actionExecutionRequestMessageModel = mapper.readValue(text, ActionExecutionRequestMessageModel.class);
            log.info("[WS] Received actionExecutionRequestMessageModel: {}", actionExecutionRequestMessageModel);
            if ("ActionExecutionRequest".equals(actionExecutionRequestMessageModel.type())) {
                log.info("[WS] Received all executors: {}", (Object) actionTaskExecutors);

                Optional<ActionTaskExecutor> taskExecutor = Arrays.stream(actionTaskExecutors)
                        .filter(executor -> executor.getActionName().equals(actionExecutionRequestMessageModel.action()))
                        .findAny();

                log.info("[WS] Found executor for action {}: {}", actionExecutionRequestMessageModel.action(), taskExecutor.isPresent());
                if (taskExecutor.isEmpty()) {
                    log.error("[WS] No executor found for action {}", actionExecutionRequestMessageModel.action());
                    registry.get().send(mapper.writeValueAsString(new ActionExecutionResultModel(
                            actionExecutionRequestMessageModel.correlationId(),
                            new ExecutorResultModel("false", Map.of(), List.of())
                    )));

                    return;
                }

                log.info("[WS] Executing action {}", actionExecutionRequestMessageModel.action());
                registry.get().send(mapper.writeValueAsString(new ActionExecutionResultModel(
                        actionExecutionRequestMessageModel.correlationId(),
                        taskExecutor.get().execute(actionExecutionRequestMessageModel)
                )));
                log.info("[WS] Action sends ws {}", actionExecutionRequestMessageModel.action());
            }
        } catch (Exception error) {
            log.error("[WS] Failed to process message: {}", text, error);
        }
    }
}
