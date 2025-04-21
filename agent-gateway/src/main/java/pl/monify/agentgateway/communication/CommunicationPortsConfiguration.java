package pl.monify.agentgateway.communication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import pl.monify.agentgateway.communication.adapter.messaging.ActionResultKafkaSender;
import pl.monify.agentgateway.communication.adapter.messaging.AgentRegisteredKafkaSender;
import pl.monify.agentgateway.communication.domain.port.out.ActionResultSenderPort;
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort;

public class CommunicationPortsConfiguration {

    @Bean
    public AgentRegisteredEventSenderPort agentRegisteredSender(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.agent-registration-topic}") String topic
    ) {
        return new AgentRegisteredKafkaSender(kafkaTemplate, topic);
    }

    @Bean
    public ActionResultSenderPort actionResultSender(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${monify.kafka.action-result-topic}") String topic
    ) {
        return new ActionResultKafkaSender(kafkaTemplate, topic);
    }
}
