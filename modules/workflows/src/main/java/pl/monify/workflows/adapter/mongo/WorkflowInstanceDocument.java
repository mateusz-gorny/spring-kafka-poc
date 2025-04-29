package pl.monify.workflows.adapter.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;
import java.util.List;

@Document("workflowInstances")
public record WorkflowInstanceDocument(
        @Id String id,
        String definitionId,
        String status,
        Map<String, Object> context,
        List<StepRecordDocument> history
) {}
