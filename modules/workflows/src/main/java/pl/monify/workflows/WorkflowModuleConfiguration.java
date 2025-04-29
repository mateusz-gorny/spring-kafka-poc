package pl.monify.workflows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.workflows.adapter.kafka.ActionExecutionRequestPublisherKafkaAdapter;
import pl.monify.workflows.adapter.kafka.WorkflowActionResponseConsumer;
import pl.monify.workflows.adapter.mongo.WorkflowDefinitionMongoAdapter;
import pl.monify.workflows.adapter.mongo.WorkflowDefinitionMongoRepository;
import pl.monify.workflows.adapter.mongo.WorkflowInstanceMongoAdapter;
import pl.monify.workflows.adapter.mongo.WorkflowInstanceMongoRepository;
import pl.monify.workflows.application.ActionInputMapper;
import pl.monify.workflows.application.DefaultNextActionResolver;
import pl.monify.workflows.application.DefaultWorkflowInstanceFactory;
import pl.monify.workflows.application.DefaultWorkflowStateUpdater;
import pl.monify.workflows.application.NextActionResolver;
import pl.monify.workflows.application.SpELActionInputMapper;
import pl.monify.workflows.application.WorkflowInstanceFactory;
import pl.monify.workflows.application.WorkflowOrchestrationService;
import pl.monify.workflows.application.WorkflowStateUpdater;
import pl.monify.workflows.port.out.ActionExecutionRequestPublisher;
import pl.monify.workflows.port.out.WorkflowDefinitionRepository;
import pl.monify.workflows.port.out.WorkflowInstanceRepository;

@Configuration
@EnableMongoRepositories
@ComponentScan(basePackageClasses = WorkflowModuleConfiguration.class)
public class WorkflowModuleConfiguration {

    @Bean
    public WorkflowDefinitionRepository workflowDefinitionRepository(WorkflowDefinitionMongoRepository mongoRepository) {
        return new WorkflowDefinitionMongoAdapter(mongoRepository);
    }

    @Bean
    public WorkflowInstanceRepository workflowInstanceRepository(WorkflowInstanceMongoRepository mongoRepository) {
        return new WorkflowInstanceMongoAdapter(mongoRepository);
    }

    @Bean
    public ActionExecutionRequestPublisher actionExecutionRequestPublisher(
            KafkaTemplate<String, Object> kafkaTemplate) {
        return new ActionExecutionRequestPublisherKafkaAdapter(kafkaTemplate);
    }

    @Bean
    public WorkflowInstanceFactory workflowInstanceFactory(
            WorkflowInstanceRepository instanceRepo,
            WorkflowDefinitionRepository definitionRepo) {
        return new DefaultWorkflowInstanceFactory(instanceRepo, definitionRepo);
    }

    @Bean
    public WorkflowStateUpdater workflowStateUpdater() {
        return new DefaultWorkflowStateUpdater();
    }

    @Bean
    public NextActionResolver nextActionResolver() {
        return new DefaultNextActionResolver();
    }

    @Bean
    public ActionInputMapper actionInputMapper() {
        return new SpELActionInputMapper();
    }

    @Bean
    public WorkflowOrchestrationService workflowOrchestrationService(
            WorkflowInstanceFactory factory,
            WorkflowStateUpdater stateUpdater,
            NextActionResolver resolver,
            ActionInputMapper mapper,
            ActionExecutionRequestPublisher publisher,
            WorkflowInstanceRepository instanceRepo) {
        return new WorkflowOrchestrationService(
                factory, stateUpdater, resolver, mapper, publisher, instanceRepo
        );
    }

    @Bean
    public WorkflowActionResponseConsumer workflowActionResponseConsumer(WorkflowOrchestrationService orchestrationService) {
        return new WorkflowActionResponseConsumer(orchestrationService);
    }
}
