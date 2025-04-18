package pl.monify.workflows;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import pl.monify.workflows.messaging.ActionExecutionRequestMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
@EnableMongoRepositories
@ComponentScan(basePackageClasses = WorkflowsModuleConfiguration.class)
public class WorkflowsModuleConfiguration {

    @Value("${monify.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${monify.kafka.group-id}")
    private String kafkaGroupId;

    @Value("${monify.kafka.auto-offset-reset:earliest}")
    private String kafkaOffsetReset;

    @Bean
    public JsonMessageConverter jsonMessageConverter() {
        return new JsonMessageConverter();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ConsumerFactory<String, ActionExecutionRequestMessage> kafkaWorkflowConsumerFactory(ObjectMapper objectMapper) {
        JsonDeserializer<ActionExecutionRequestMessage> deserializer =
                new JsonDeserializer<>(ActionExecutionRequestMessage.class, objectMapper);
        deserializer.addTrustedPackages(
                "pl.monify",
                "java.util"
        );

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaOffsetReset);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ActionExecutionRequestMessage> kafkaWorkflowListenerContainerFactory(
            ConsumerFactory<String, ActionExecutionRequestMessage> kafkaConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ActionExecutionRequestMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> kafkaWorkflowProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }
}
