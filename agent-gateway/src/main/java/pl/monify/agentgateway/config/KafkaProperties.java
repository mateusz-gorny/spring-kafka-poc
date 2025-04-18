package pl.monify.agentgateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

/**
 * Configuration properties for Kafka.
 */
@ConfigurationProperties(prefix = "monify.kafka")
public record KafkaProperties(
        String bootstrapServers,
        String groupId,
        String autoOffsetReset,
        String actionExecutionRequestTopic,
        String actionResultTopic,
        String agentRegistrationTopic
) {
    @ConstructorBinding
    public KafkaProperties {
        if (autoOffsetReset == null) {
            autoOffsetReset = "earliest";
        }
        if (actionExecutionRequestTopic == null) {
            actionExecutionRequestTopic = "action.execution.request";
        }
        if (actionResultTopic == null) {
            actionResultTopic = "workflow.action.response";
        }
        if (agentRegistrationTopic == null) {
            agentRegistrationTopic = "agent.registration.event";
        }
    }
}