package pl.monify.credentialstore.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.monify.credentialstore.model.CredentialEntity;

@Repository
public interface MongoCredentialRepository extends MongoRepository<CredentialEntity, String> {
}
