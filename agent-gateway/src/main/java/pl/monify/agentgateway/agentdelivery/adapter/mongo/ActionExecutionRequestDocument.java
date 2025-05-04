package pl.monify.agentgateway.agentdelivery.adapter.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("action_execution_requests")
public record ActionExecutionRequestDocument(
        @Id String id,
        String actionName,
        String teamId,
        String payload,
        @Indexed(expireAfter = "PT10S") Instant expiration
) {}
