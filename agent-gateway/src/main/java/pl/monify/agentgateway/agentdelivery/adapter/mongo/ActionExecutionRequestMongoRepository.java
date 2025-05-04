package pl.monify.agentgateway.agentdelivery.adapter.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ActionExecutionRequestMongoRepository extends MongoRepository<ActionExecutionRequestDocument, String> {
    Optional<ActionExecutionRequestDocument> findByTeamIdAndActionName(String teamId, String actionName);
}
