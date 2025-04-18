package pl.monify.workflows.model;

import java.util.Map;

public class ActionInstance {
    private String actionId;
    private String name;
    private WorkflowActionDefinition actionDefinition;
    private String status;
    private String log;
    private Map<String, Object> output;

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkflowActionDefinition getActionDefinition() {
        return actionDefinition;
    }

    public void setActionDefinition(WorkflowActionDefinition actionDefinition) {
        this.actionDefinition = actionDefinition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Map<String, Object> getOutput() {
        return output;
    }

    public void setOutput(Map<String, Object> output) {
        this.output = output;
    }


    @Override
    public String toString() {
        return "ActionInstance{" +
                "actionId='" + actionId + '\'' +
                ", name='" + name + '\'' +
                ", actionDefinition=" + actionDefinition +
                ", status='" + status + '\'' +
                ", log='" + log + '\'' +
                ", output=" + output +
                '}';
    }
}