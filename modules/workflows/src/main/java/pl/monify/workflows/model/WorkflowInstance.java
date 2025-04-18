package pl.monify.workflows.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document("workflow_instances")
public class WorkflowInstance {

    @Id
    private String id;

    private String workflowId;
    private Status status;
    private String triggeredBy;
    private Map<String, Object> payload;

    private List<ActionInstance> actions;
    private Instant startedAt;
    private Instant finishedAt;
    private String teamId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public List<ActionInstance> getActions() {
        return actions;
    }

    public void setActions(List<ActionInstance> actions) {
        this.actions = actions;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    public enum Status {
        QUEUED, IN_PROGRESS, FINISHED, FAILED
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "WorkflowInstance{" +
                "id='" + id + '\'' +
                ", workflowId='" + workflowId + '\'' +
                ", status=" + status +
                ", triggeredBy='" + triggeredBy + '\'' +
                ", payload=" + payload +
                ", actions=" + actions +
                ", startedAt=" + startedAt +
                ", finishedAt=" + finishedAt +
                ", teamId='" + teamId + '\'' +
                '}';
    }
}
