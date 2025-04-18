package pl.monify.workflows.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.monify.workflows.messaging.ActionExecutionRequestMessage;
import pl.monify.workflows.model.ActionInstance;
import pl.monify.workflows.model.WorkflowActionDefinition;
import pl.monify.workflows.model.WorkflowDefinition;
import pl.monify.workflows.model.WorkflowInstance;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class WorkflowEngineService {

    private static final Logger log = LoggerFactory.getLogger(WorkflowEngineService.class);
    private final WorkflowInstanceService instanceService;
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String ACTION_EXECUTION_TOPIC = "action.execution.request";

    public WorkflowEngineService(
            WorkflowInstanceService instanceService,
            KafkaTemplate<Object, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.instanceService = instanceService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void execute(WorkflowInstance instance, WorkflowDefinition definition) {
        log.info("Executing workflow {}", instance.getId());
        instance.setStatus(WorkflowInstance.Status.IN_PROGRESS);
        instance.setStartedAt(Instant.now());
        instance.setActions(new ArrayList<>());

        for (WorkflowActionDefinition action : definition.getActions()) {
            log.info("Executing action {}", action.name());
            String actionInstanceId = UUID.randomUUID().toString();

            ActionInstance actionInst = new ActionInstance();
            actionInst.setActionId(actionInstanceId);
            actionInst.setName(action.name());
            actionInst.setActionDefinition(action);

            if (action.agentId() != null && !action.agentId().isBlank()) {
                log.info("Action {} has agentId {}", action.name(), action.agentId());
                actionInst.setStatus("QUEUED");

                ActionExecutionRequestMessage payload = new ActionExecutionRequestMessage(
                        instance.getId(),
                        action.name(),
//                        instance.getTeamId(),
                        "FirstTeam",
                        actionInstanceId,
                        action.input()
                );

                log.info("Sending message to Kafka topic {}: {}", ACTION_EXECUTION_TOPIC, payload);
                try {
                    kafkaTemplate.send(ACTION_EXECUTION_TOPIC, objectMapper.writeValueAsString(payload));
                } catch (JsonProcessingException e) {
                    log.error("Failed to serialize message: {}", e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            } else {
                actionInst.setStatus("FAILED");
                actionInst.setLog("Missing agentId for action: " + action.name());
            }

            instance.getActions().add(actionInst);
        }

        instanceService.save(instance);
    }
}
