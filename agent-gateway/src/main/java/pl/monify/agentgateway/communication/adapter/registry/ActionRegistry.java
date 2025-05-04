package pl.monify.agentgateway.communication.adapter.registry;

import org.springframework.stereotype.Component;
import pl.monify.agentgateway.communication.domain.model.AgentRegisterModel;
import pl.monify.agentgateway.communication.domain.port.out.ActionRegistryPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentSessionFinderPort;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActionRegistry implements ActionRegistryPort, AgentSessionFinderPort {

    private final Map<String, RegisteredAction> registry = new ConcurrentHashMap<>();

    @Override
    public void register(AgentRegisterModel agentRegisterModel) {
        registry.put(agentRegisterModel.teamId() + ":" + agentRegisterModel.actionName(), new RegisteredAction(
                agentRegisterModel.session(),
                agentRegisterModel.inputSchema(),
                agentRegisterModel.outputSchema(),
                agentRegisterModel.actionType(),
                agentRegisterModel.ttl()
        ));
    }

    public Optional<RegisteredAction> find(String teamId, String actionName) {
        return Optional.ofNullable(registry.get(teamId + ":" + actionName));
    }
}
