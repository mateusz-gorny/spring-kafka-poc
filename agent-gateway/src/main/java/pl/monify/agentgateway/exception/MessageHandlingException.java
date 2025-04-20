package pl.monify.agentgateway.exception;

/**
 * Exception thrown when there's an error handling a message.
 */
public class MessageHandlingException extends AgentGatewayException {

    private final String messageType;

    public MessageHandlingException(String messageType, String message) {
        super("Error handling message of type '" + messageType + "': " + message);
        this.messageType = messageType;
    }

    public MessageHandlingException(String messageType, String message, Throwable cause) {
        super("Error handling message of type '" + messageType + "': " + message, cause);
        this.messageType = messageType;
    }

    public String getMessageType() {
        return messageType;
    }
}