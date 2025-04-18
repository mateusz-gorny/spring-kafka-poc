package pl.monify.workflows.service;

import org.springframework.stereotype.Service;
import pl.monify.workflows.model.WorkflowInstance;
import pl.monify.workflows.repository.WorkflowInstanceRepository;

import java.util.Optional;

@Service
public class WorkflowInstanceService {

    private final WorkflowInstanceRepository repository;

    public WorkflowInstanceService(WorkflowInstanceRepository repository) {
        this.repository = repository;
    }

    public WorkflowInstance save(WorkflowInstance instance) {
        return repository.save(instance);
    }

    public Optional<WorkflowInstance> getById(String id) {
        return repository.findById(id);
    }

    public Optional<WorkflowInstance> findByActionInstanceId(String actionInstanceId) {
        return repository.findAll().stream()
                .filter(inst -> inst.getActions() != null &&
                        inst.getActions().stream().anyMatch(a -> a.getActionId().equals(actionInstanceId)))
                .findFirst();
    }
}
