package pl.monify.workflows.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.monify.workflows.events.ActionExecutionRequestEvent;
import pl.monify.workflows.events.WorkflowActionResponseEvent;
import pl.monify.workflows.port.in.WorkflowActionResponseHandler;
import pl.monify.workflows.port.out.ActionExecutionRequestPublisher;
import pl.monify.workflows.port.out.WorkflowInstanceRepository;

import java.util.UUID;

public final class WorkflowOrchestrationService implements WorkflowActionResponseHandler {

    private static final Logger log = LoggerFactory.getLogger(WorkflowOrchestrationService.class);
    private final WorkflowInstanceFactory factory;
    private final WorkflowStateUpdater stateUpdater;
    private final NextActionResolver resolver;
    private final ActionInputMapper mapper;
    private final ActionExecutionRequestPublisher publisher;
    private final WorkflowInstanceRepository instanceRepo;

    public WorkflowOrchestrationService(WorkflowInstanceFactory factory,
                                        WorkflowStateUpdater stateUpdater,
                                        NextActionResolver resolver,
                                        ActionInputMapper mapper,
                                        ActionExecutionRequestPublisher publisher,
                                        WorkflowInstanceRepository instanceRepo) {
        this.factory = factory;
        this.stateUpdater = stateUpdater;
        this.resolver = resolver;
        this.mapper = mapper;
        this.publisher = publisher;
        this.instanceRepo = instanceRepo;
    }

    @Override
    public void handle(WorkflowActionResponseEvent event) {
        log.info("Received action response: {}", event);
        var instance = factory.loadOrCreate(
                event.correlationId(),
                event.workflowDefinitionId()
        );

        log.info("Applying step");
        var updated = stateUpdater.applyStep(instance, event);

        log.info("Resolving...");
        var nextDefs = resolver.resolve(updated.definition(), event.actionName());

        log.info("Before loop mapper");
        for (var def : nextDefs) {
            log.info("Mapping...");
            var nextInput = mapper.map(def, updated.context(), event.output());

            log.info("Publishing");
            publisher.publish(new ActionExecutionRequestEvent(
                    event.workflowDefinitionId(),
                    event.workflowInstanceId(),
                    def.actionName(),
                    "team-abc",
                    UUID.randomUUID().toString(),
                    nextInput
            ));
        }

        log.info("Saving...");
        instanceRepo.save(updated);
    }
}
