package pl.monify.agentgateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import pl.monify.agentgateway.communication.domain.port.out.ActionResultSenderPort
import pl.monify.agentgateway.communication.domain.port.out.AgentRegisteredEventSenderPort
import spock.lang.Specification
import util.TestEventObserverRegistry

@Configuration
@Profile("test")
class TestMocksConfig extends Specification {
    static TestEventObserverRegistry observerRegistry = new TestEventObserverRegistry()

    @Bean
    @Primary
    AgentRegisteredEventSenderPort agentRegisteredSender() {
        return (agent) -> {
            observerRegistry.notifyAll(AgentRegisteredEventSenderPort.class, agent)
        }
    }

    @Bean
    @Primary
    ActionResultSenderPort actionResultSender() {
        return (map) -> {
            observerRegistry.notifyAll(ActionResultSenderPort.class, map)
        }
    }
}
