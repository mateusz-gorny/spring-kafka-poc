package pl.monify.agentgateway.communication.domain.port.out;

public interface ConnectionRateLimiterPort {
    boolean allowConnection();
}
