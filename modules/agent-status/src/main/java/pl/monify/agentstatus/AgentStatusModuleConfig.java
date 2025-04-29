package pl.monify.agentstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.agentstatus.adapter.kafka.AgentCreatedKafkaPublisher;
import pl.monify.agentstatus.adapter.kafka.AgentPingKafkaListener;
import pl.monify.agentstatus.adapter.mongo.AgentPingPersistenceAdapter;
import pl.monify.agentstatus.adapter.mongo.AgentPingRepository;
import pl.monify.agentstatus.adapter.mongo.AgentSecretRepository;
import pl.monify.agentstatus.application.AgentPingHandlerService;
import pl.monify.agentstatus.application.CreateAgentService;
import pl.monify.agentstatus.domain.port.out.AgentCreatedEventPublisherPort;
import pl.monify.agentstatus.domain.port.out.AgentPingPersistencePort;

@Configuration
@EnableMongoRepositories
@ComponentScan(basePackageClasses = AgentStatusModuleConfig.class)
public class AgentStatusModuleConfig {

    @Bean
    public AgentPingPersistencePort agentPingPersistencePort(AgentPingRepository repository) {
        return new AgentPingPersistenceAdapter(repository);
    }

    @Bean
    public AgentPingHandlerService agentPingHandlerService(AgentPingPersistencePort persistencePort) {
        return new AgentPingHandlerService(persistencePort);
    }

    @Bean
    public AgentPingKafkaListener agentPingKafkaListener(AgentPingHandlerService service) {
        return new AgentPingKafkaListener(service);
    }

    @Bean
    public AgentCreatedEventPublisherPort agentCreatedEventPublisherPort(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.agent-event-created-topic}") String topic
    ) {
        return new AgentCreatedKafkaPublisher(kafkaTemplate, topic);
    }

    @Bean
    public CreateAgentService createAgentService(
            AgentSecretRepository repository,
            AgentCreatedEventPublisherPort publisherPort
    ) {
        return new CreateAgentService(repository, publisherPort);
    }
}
