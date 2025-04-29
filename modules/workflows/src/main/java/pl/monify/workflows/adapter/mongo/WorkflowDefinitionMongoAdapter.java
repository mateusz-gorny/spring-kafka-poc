package pl.monify.workflows.adapter.mongo;

import pl.monify.workflows.domain.WorkflowDefinition;
import pl.monify.workflows.port.out.WorkflowDefinitionRepository;

import java.util.List;
import java.util.Optional;

public class WorkflowDefinitionMongoAdapter implements WorkflowDefinitionRepository {

    private final WorkflowDefinitionMongoRepository mongoRepository;

    public WorkflowDefinitionMongoAdapter(WorkflowDefinitionMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Optional<WorkflowDefinition> findById(String id) {
        return mongoRepository.findById(id);
    }

    @Override
    public Optional<WorkflowDefinition> findByCorrelationId(String correlationId) {
        return mongoRepository.findByCorrelationId(correlationId);
    }

    @Override
    public List<WorkflowDefinition> findAll() {
        return mongoRepository.findAll();
    }

    @Override
    public WorkflowDefinition save(WorkflowDefinition definition) {
        return mongoRepository.save(definition);
    }

    @Override
    public void delete(String id) {
        mongoRepository.deleteById(id);
    }
}
