package pl.monify.workflows.adapter.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;
import java.util.List;

@Document("workflowDefinitions")
public record WorkflowDefinitionDocument(
        @Id String id,
        Map<String, List<NextActionDefinitionDocument>> transitions
) {}
