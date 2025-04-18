package pl.monify.agentgateway.exception;

/**
 * Exception thrown when there's an error with Kafka operations.
 */
public class KafkaException extends AgentGatewayException {
    
    private final String topic;
    private final String operation;
    
    public KafkaException(String topic, String operation, String message) {
        super("Kafka error for topic '" + topic + "' during " + operation + ": " + message);
        this.topic = topic;
        this.operation = operation;
    }
    
    public KafkaException(String topic, String operation, String message, Throwable cause) {
        super("Kafka error for topic '" + topic + "' during " + operation + ": " + message, cause);
        this.topic = topic;
        this.operation = operation;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public String getOperation() {
        return operation;
    }
}