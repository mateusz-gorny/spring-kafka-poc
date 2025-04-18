package pl.monify.agentgateway.exception;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends AgentGatewayException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}