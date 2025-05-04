package pl.monify.agentgateway.communication.web.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.agentdelivery.adapter.mongo.ActionExecutionRequestDocument;
import pl.monify.agentgateway.agentdelivery.adapter.mongo.ActionExecutionRequestMongoRepository;
import pl.monify.agentgateway.agentdelivery.domain.application.AgentDispatcher;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;
import pl.monify.agentgateway.communication.domain.model.AgentSession;
import pl.monify.agentgateway.communication.web.AgentMessageHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

public final class RequestTaskHandler implements AgentMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestTaskHandler.class);

    private final ObjectMapper mapper;
    private final ActionExecutionRequestMongoRepository repository;
    private final AgentDispatcher dispatcher;

    public RequestTaskHandler(
            ObjectMapper mapper,
            AgentDispatcher dispatcher,
            ActionExecutionRequestMongoRepository repository
    ) {
        this.mapper = mapper;
        this.repository = repository;
        this.dispatcher = dispatcher;
    }

    @Override
    public String type() {
        return "request-task";
    }

    @Override
    public Mono<Void> handle(String payload, AgentSession session) throws Exception {
        log.info("[TASK] Received request-task message {}", payload);
        var node = mapper.readTree(payload);
        var actionName = node.get("actionName").asText();
        var ttl = node.has("ttl") ? Duration.parse(node.get("ttl").asText()) : Duration.ofMinutes(5);
        var teamId = session.teamId();

        return Mono.fromCallable(() -> {
                    log.info("[TASK] Processing request-task message for team={} action={}", teamId, actionName);
                    var doc = repository.findByTeamIdAndActionName(teamId, actionName).orElse(null);
                    if (doc == null) {
                        log.error("[TASK] No input for team={} action={}", teamId, actionName);
                        return "{\"type\":\"no-task\",\"action\":\"" + actionName + "\"}";
                    }

                    log.info("[TASK] Processing request-task message for team={} action={} ttl={}", teamId, actionName, ttl);
                    var updated = new ActionExecutionRequestDocument(
                            doc.id(),
                            doc.actionName(),
                            doc.teamId(),
                            doc.payload(),
                            Instant.now().plus(ttl).plusSeconds(60)
                    );
                    repository.save(updated);
                    ActionExecutionRequestMessage message = null;

                    try {
                        message = mapper.readValue(doc.payload(), ActionExecutionRequestMessage.class);
                        dispatcher.dispatch(message);
                        log.info("[TASK] Redispatched request to agent: correlationId={}, team={}, action={}",
                                message.correlationId(), message.teamId(), message.action());
                    } catch (JsonProcessingException error) {
                        log.error("[TASK] Failed to deserialize payload for team={} action={}", teamId, actionName, error);
                    }

                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
