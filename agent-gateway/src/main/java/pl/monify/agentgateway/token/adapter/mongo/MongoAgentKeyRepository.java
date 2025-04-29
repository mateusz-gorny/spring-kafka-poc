package pl.monify.agentgateway.token.adapter.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoAgentKeyRepository extends MongoRepository<MongoAgentKeyDocument, String> {
    Optional<MongoAgentKeyDocument> findByAgentId(String agentId);
}
