package pl.monify.agentgateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import pl.monify.agentgateway.config.JwtProperties;
import pl.monify.agentgateway.config.KafkaProperties;
import pl.monify.agentgateway.config.WebSocketProperties;
import pl.monify.agentgateway.messaging.ActionExecutionRequestMessage;
import pl.monify.agentgateway.socket.AgentWebSocketHandler;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties({
        KafkaProperties.class,
        JwtProperties.class,
        WebSocketProperties.class
})
public class AgentGatewayModuleConfiguration {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${monify.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${monify.kafka.group-id}")
    private String kafkaGroupId;

    @Value("${monify.kafka.auto-offset-reset:earliest}")
    private String kafkaOffsetReset;

    @Bean
    public HandlerMapping agentWebSocketHandlerMapping(AgentWebSocketHandler handler) {
        return new SimpleUrlHandlerMapping(Map.of("/ws/agent", handler), 1);
    }

    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public SecretKey jwtSecretKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    @Bean
    public JwtParser jwtParser(SecretKey jwtSecretKey) {
        return Jwts.parser().verifyWith(jwtSecretKey).build();
    }

    @Bean
    public ConsumerFactory<String, ActionExecutionRequestMessage> kafkaConsumerFactory(ObjectMapper objectMapper) {
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
    public ConcurrentKafkaListenerContainerFactory<String, ActionExecutionRequestMessage> kafkaListenerContainerFactory(
            ConsumerFactory<String, ActionExecutionRequestMessage> kafkaConsumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ActionExecutionRequestMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory);
        return factory;
    }

    @Bean
    public ProducerFactory<String, Object> kafkaProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), new JsonSerializer<>(objectMapper));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }
}
