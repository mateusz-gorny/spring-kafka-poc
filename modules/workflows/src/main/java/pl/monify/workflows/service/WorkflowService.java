package pl.monify.workflows.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.monify.workflows.model.WorkflowDefinition;
import pl.monify.workflows.repository.WorkflowInstanceRepository;
import pl.monify.workflows.repository.WorkflowRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WorkflowService {
    private final WorkflowRepository repository;
    private final WorkflowInstanceRepository instanceRepository;

    public WorkflowService(WorkflowRepository repository, WorkflowInstanceRepository instanceRepository) {
        this.repository = repository;
        this.instanceRepository = instanceRepository;
    }

    public WorkflowDefinition create(WorkflowDefinition definition) {
        return repository.save(definition);
    }

    public List<WorkflowDefinition> list(boolean includeArchived) {
        if (includeArchived) {
            return repository.findAll();
        }
        return repository.findByStatusNot(WorkflowDefinition.WorkflowStatus.ARCHIVED);
    }

    public Optional<WorkflowDefinition> getById(String id) {
        return repository.findById(id);
    }

    public WorkflowDefinition update(WorkflowDefinition updated) {
        return repository.save(updated);
    }

    @Transactional
    public void delete(String id) {
        instanceRepository.deleteById(id);
        repository.deleteById(id);
    }
}
