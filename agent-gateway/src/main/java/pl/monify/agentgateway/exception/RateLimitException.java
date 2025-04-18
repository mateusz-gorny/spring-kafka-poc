package pl.monify.agentgateway.exception;

/**
 * Exception thrown when rate limits are exceeded.
 */
public class RateLimitException extends AgentGatewayException {
    
    private final String resourceType;
    private final int limit;
    
    public RateLimitException(String resourceType, int limit) {
        super("Rate limit exceeded for " + resourceType + ": " + limit + " per second");
        this.resourceType = resourceType;
        this.limit = limit;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public int getLimit() {
        return limit;
    }
}