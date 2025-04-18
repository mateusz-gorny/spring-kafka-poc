package pl.monify.agentgateway.exception;

/**
 * Base exception class for all agent gateway exceptions.
 */
public class AgentGatewayException extends RuntimeException {
    
    public AgentGatewayException(String message) {
        super(message);
    }
    
    public AgentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}