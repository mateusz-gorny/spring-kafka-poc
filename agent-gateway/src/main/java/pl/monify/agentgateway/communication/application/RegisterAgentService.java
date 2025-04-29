package pl.monify.agentgateway.communication.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.agentgateway.communication.domain.model.AgentRegisterModel;
import pl.monify.agentgateway.communication.domain.model.AgentRegisteredMessage;
import pl.monify.agentgateway.communication.domain.port.in.RegisterAgentUseCase;
import pl.monify.agentgateway.communication.domain.port.out.ActionRegistryPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort;

public class RegisterAgentService implements RegisterAgentUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterAgentService.class);
    private final ActionRegistryPort actionRegistry;
    private final AgentRegisteredEventSenderPort eventSender;

    public RegisterAgentService(ActionRegistryPort actionRegistry,
                                AgentRegisteredEventSenderPort eventSender) {
        this.actionRegistry = actionRegistry;
        this.eventSender = eventSender;
    }

    @Override
    public void register(AgentRegisterModel agentRegisterModel) {
        log.info("[WS] Received register message for agent {} for team {}", agentRegisterModel.session().id(), agentRegisterModel.teamId());
        actionRegistry.register(agentRegisterModel);

        AgentRegisteredMessage event = new AgentRegisteredMessage(
                agentRegisterModel.agentId(),
                agentRegisterModel.actionName(),
                agentRegisterModel.session().id(),
                agentRegisterModel.teamId(),
                agentRegisterModel.inputSchema(),
                agentRegisterModel.outputSchema()
        );
        log.info("[WS] Sending agent registered event for agent {} for team {}", agentRegisterModel.session().id(), agentRegisterModel.teamId());
        eventSender.send(event);
    }
}
