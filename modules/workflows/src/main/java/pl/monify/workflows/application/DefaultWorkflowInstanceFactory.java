package pl.monify.workflows.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.workflows.domain.WorkflowDefinition;
import pl.monify.workflows.domain.WorkflowInstance;
import pl.monify.workflows.domain.WorkflowStatus;
import pl.monify.workflows.port.out.WorkflowDefinitionRepository;
import pl.monify.workflows.port.out.WorkflowInstanceRepository;

import java.util.HashMap;
import java.util.List;

public final class DefaultWorkflowInstanceFactory implements WorkflowInstanceFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultWorkflowInstanceFactory.class);
    private final WorkflowInstanceRepository instanceRepo;
    private final WorkflowDefinitionRepository definitionRepo;

    public DefaultWorkflowInstanceFactory(WorkflowInstanceRepository instanceRepo,
                                          WorkflowDefinitionRepository definitionRepo) {
        this.instanceRepo = instanceRepo;
        this.definitionRepo = definitionRepo;
    }

    @Override
    public WorkflowInstance loadOrCreate(String correlationId, String workflowDefinitionId) {
        log.info("Loading or creating workflow instance for correlationId: {}, workflowDefinitionId: {}", correlationId, workflowDefinitionId);
        return instanceRepo.findById(correlationId)
                .orElseGet(() -> {
                    log.info("Creating new instance: {}", correlationId);
                    WorkflowDefinition def = definitionRepo.findByCorrelationId(correlationId)
                            .orElseThrow(() -> new RuntimeException("Workflow definition not found: " + workflowDefinitionId));
                    log.info("Created new instance: {}", def);
                    WorkflowInstance inst = new WorkflowInstance(
                            correlationId,
                            def,
                            WorkflowStatus.RUNNING,
                            new HashMap<>(),
                            List.of()
                    );
                    log.info("Saving new instance: {}", inst);
                    instanceRepo.save(inst);
                    log.info("Saved new instance: {}", inst);
                    return inst;
                });
    }
}
