package pl.monify.workflows.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document("workflow_definitions")
public class WorkflowDefinition {

    @Id
    private String id;

    private String name;
    private WorkflowStatus status;
    private List<String> triggerIds;
    private List<String> credentialIds;
    private List<WorkflowActionDefinition> actions;

    private Instant createdAt;
    private Instant updatedAt;

    public WorkflowDefinition() {}

    public WorkflowDefinition(String id, String name, WorkflowStatus status, List<String> triggerIds, List<String> credentialIds, List<WorkflowActionDefinition> actions, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.triggerIds = triggerIds;
        this.credentialIds = credentialIds;
        this.actions = actions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public void setTriggerIds(List<String> triggerIds) {
        this.triggerIds = triggerIds;
    }

    public void setCredentialIds(List<String> credentialIds) {
        this.credentialIds = credentialIds;
    }

    public void setActions(List<WorkflowActionDefinition> actions) {
        this.actions = actions;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public List<String> getTriggerIds() {
        return triggerIds;
    }

    public List<String> getCredentialIds() {
        return credentialIds;
    }

    public List<WorkflowActionDefinition> getActions() {
        return actions;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public enum WorkflowStatus {
        ACTIVE, INACTIVE, ARCHIVED
    }


}
