package pl.monify.agentgateway.communication.domain.port.out;

public interface MessageRateLimiterPort {
    boolean isRateLimited(String agentId);
}
