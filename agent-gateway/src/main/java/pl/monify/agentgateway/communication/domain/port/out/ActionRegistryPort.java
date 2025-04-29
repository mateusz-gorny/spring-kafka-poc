package pl.monify.agentgateway.communication.domain.port.out;

import pl.monify.agentgateway.communication.domain.model.AgentRegisterModel;

public interface ActionRegistryPort {
    void register(AgentRegisterModel agentRegisterModel);
}
