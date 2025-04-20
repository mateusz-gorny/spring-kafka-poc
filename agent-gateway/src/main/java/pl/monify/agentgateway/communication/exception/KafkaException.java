package pl.monify.agentgateway.communication.exception;

public class KafkaException extends RuntimeException {
    public KafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}
