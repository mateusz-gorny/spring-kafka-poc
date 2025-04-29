package pl.monify.agentstatus.adapter.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgentSecretRepository extends MongoRepository<AgentSecretDocument, String> {
}
