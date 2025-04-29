package pl.monify.agentgateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import pl.monify.agentgateway.agentdelivery.domain.model.ActionExecutionRequestMessage;
import pl.monify.agentgateway.communication.adapter.messaging.ActionResultKafkaSender;
import pl.monify.agentgateway.communication.adapter.messaging.AgentPingKafkaSender;
import pl.monify.agentgateway.communication.adapter.messaging.AgentRegisteredKafkaSender;
import pl.monify.agentgateway.communication.domain.port.out.ActionResultSenderPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort;
import pl.monify.agentgateway.token.domain.event.AgentCreatedEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfiguration {
    @Value("${monify.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${monify.kafka.group-id}")
    private String kafkaGroupId;

    @Value("${monify.kafka.auto-offset-reset:earliest}")
    private String kafkaOffsetReset;

    private <T> ConsumerFactory<String, T> buildConsumerFactory(Class<T> clazz, ObjectMapper objectMapper, String trustedPackage) {
        JsonDeserializer<T> deserializer =
                new JsonDeserializer<>(clazz, objectMapper);
        deserializer.addTrustedPackages(
                "pl.monify",
                trustedPackage,
                "java.util"
        );
        deserializer.setRemoveTypeHeaders(true);
        deserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaOffsetReset);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> buildListenerFactory(ConsumerFactory<String, T> factory) {
        ConcurrentKafkaListenerContainerFactory<String, T> listenerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerFactory.setConsumerFactory(factory);
        return listenerFactory;
    }

    @Bean
    public ConsumerFactory<String, ActionExecutionRequestMessage> kafkaActionExecutionConsumerFactory(ObjectMapper objectMapper) {
        return buildConsumerFactory(ActionExecutionRequestMessage.class, objectMapper, "pl.monify.agentgateway.agentdelivery.domain.model");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ActionExecutionRequestMessage> kafkaActionExecutionListenerContainerFactory(
            ConsumerFactory<String, ActionExecutionRequestMessage> kafkaConsumerFactory
    ) {
        return buildListenerFactory(kafkaConsumerFactory);
    }

    @Bean
    public ConsumerFactory<String, AgentCreatedEvent> kafkaAgentCreatedEventConsumerFactory(ObjectMapper objectMapper) {
        return buildConsumerFactory(AgentCreatedEvent.class, objectMapper, "pl.monify.agentgateway.token.domain.event");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AgentCreatedEvent> kafkaAgentCreatedEventListenerContainerFactory(
            ConsumerFactory<String, AgentCreatedEvent> kafkaConsumerFactory
    ) {
        return buildListenerFactory(kafkaConsumerFactory);
    }

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        JsonSerializer<Object> serializer = new JsonSerializer<>(objectMapper);
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), serializer);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @Bean
    public AgentRegisteredEventSenderPort agentRegisteredSender(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.agent-registration-topic}") String topic
    ) {
        return new AgentRegisteredKafkaSender(kafkaTemplate, topic);
    }

    @Bean
    public AgentPingKafkaSender agentPingKafkaSender(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.agent-ping-topic}") String topic
    ) {
        return new AgentPingKafkaSender(kafkaTemplate, topic);
    }

    @Bean
    public ActionResultSenderPort actionResultSender(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.action-result-topic}") String topic
    ) {
        return new ActionResultKafkaSender(kafkaTemplate, topic);
    }
}
