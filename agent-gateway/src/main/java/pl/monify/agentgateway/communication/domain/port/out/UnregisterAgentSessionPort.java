package pl.monify.agentgateway.communication.domain.port.out;

public interface UnregisterAgentSessionPort {
    void unregister(String agentId);
}
