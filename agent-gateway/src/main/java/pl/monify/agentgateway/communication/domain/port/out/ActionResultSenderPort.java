package pl.monify.agentgateway.communication.domain.port.out;

import java.util.Map;

public interface ActionResultSenderPort {
    void send(Map<String, Object> result);
}
