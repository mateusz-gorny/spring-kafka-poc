package pl.monify.agentgateway.communication.domain.port.in;

import pl.monify.agentgateway.communication.domain.model.AgentRegisterModel;

public interface RegisterAgentUseCase {
    void register(AgentRegisterModel agentRegisterModel);
}
