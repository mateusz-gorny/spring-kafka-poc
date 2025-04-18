package pl.monify.triggers.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.monify.triggers.model.TriggerEntity;

import java.util.Optional;

public interface TriggerRepository extends MongoRepository<TriggerEntity, String> {
    Optional<TriggerEntity> findByKey(String key);
}
