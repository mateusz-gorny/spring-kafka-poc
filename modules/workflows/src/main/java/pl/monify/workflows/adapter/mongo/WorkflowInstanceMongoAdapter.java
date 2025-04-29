package pl.monify.workflows.adapter.mongo;

import pl.monify.workflows.domain.StepRecord;
import pl.monify.workflows.domain.WorkflowDefinition;
import pl.monify.workflows.domain.WorkflowInstance;
import pl.monify.workflows.domain.WorkflowStatus;
import pl.monify.workflows.port.out.WorkflowInstanceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkflowInstanceMongoAdapter implements WorkflowInstanceRepository {

    private final WorkflowInstanceMongoRepository mongoRepository;

    public WorkflowInstanceMongoAdapter(WorkflowInstanceMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<WorkflowInstance> findById(String id) {
        return mongoRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<WorkflowInstance> findAll() {
        return mongoRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<WorkflowInstance> findByDefinitionId(String definitionId) {
        return mongoRepository.findByDefinitionId(definitionId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void save(WorkflowInstance instance) {
        mongoRepository.save(toDocument(instance));
    }

    private WorkflowInstance toDomain(WorkflowInstanceDocument document) {
        return new WorkflowInstance(
                document.id(),
                new WorkflowDefinition(document.definitionId(), UUID.randomUUID().toString(), null),
                WorkflowStatus.valueOf(document.status()),
                document.context(),
                document.history().stream()
                        .map(this::toStepRecord)
                        .collect(Collectors.toList())
        );
    }

    private WorkflowInstanceDocument toDocument(WorkflowInstance instance) {
        return new WorkflowInstanceDocument(
                instance.workflowInstanceId(),
                instance.definition().id(),
                instance.status().name(),
                instance.context(),
                instance.history().stream()
                        .map(this::toStepRecordDocument)
                        .collect(Collectors.toList())
        );
    }

    private StepRecord toStepRecord(StepRecordDocument document) {
        return new StepRecord(
                document.actionName(),
                document.input(),
                document.output(),
                document.timestamp(),
                WorkflowStatus.valueOf(document.status())
        );
    }

    private StepRecordDocument toStepRecordDocument(StepRecord record) {
        return new StepRecordDocument(
                record.actionName(),
                record.input(),
                record.output(),
                record.timestamp(),
                record.status().name()
        );
    }
}
