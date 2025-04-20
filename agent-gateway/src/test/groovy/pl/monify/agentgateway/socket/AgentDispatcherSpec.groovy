package pl.monify.agentgateway.socket

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.reactive.socket.WebSocketSession
import pl.monify.agentgateway.messaging.ActionExecutionRequest
import pl.monify.agentgateway.messaging.AgentDispatcher
import pl.monify.agentgateway.testutil.MockAgentSession
import reactor.core.publisher.Mono
import spock.lang.Specification

class AgentDispatcherSpec extends Specification {
    def registry = Mock(ActionRegistry)

    def setupDispatcherWithAgent(ObjectMapper objectMapper) {
        return new AgentDispatcher(objectMapper, registry)
    }

    def "should dispatch if agent is registered and matches action + team"() {
        given:
        def session = Mock(WebSocketSession)
        def agent = new MockAgentSession("send-email", "team-1", session)
        registry.find(_ as String, _ as String) >> Optional.of(new ActionRegistry.RegisteredAction(
                "send-email",
                agent,
                null,
                null
        ))
        def dispatcher = setupDispatcherWithAgent(new ObjectMapper())

        def request = new ActionExecutionRequest("ActionExecutionRequest", "cid-1",
                new ActionExecutionRequest.Payload("workflow-1", "send-email", [a: "b"]))

        when:
        def result = dispatcher.dispatch(request, "send-email", "team-1")

        then:
        result
        agent.sentMessages.size() == 1
        agent.sentMessages[0].contains("workflow-1")
    }

    def "should log and return true even if sendText throws"() {
        given:
        def session = Mock(WebSocketSession)
        def agent = new MockAgentSession("send-email", "team-1", session) {
            @Override
            Mono<Void> sendText(String msg) {
                return Mono.error(new RuntimeException("send failed"))
            }
        }
        registry.find(_ as String, _ as String) >> Optional.of(new ActionRegistry.RegisteredAction(
                "send-email",
                agent,
                null,
                null
        ))

        def dispatcher = new AgentDispatcher(new ObjectMapper(), registry)

        def request = new ActionExecutionRequest("ActionExecutionRequest", "cid-1",
                new ActionExecutionRequest.Payload("workflow-1", "send-email", [a: "b"]))

        when:
        def result = dispatcher.dispatch(request, "send-email", "team-1")

        then:
        result
        noExceptionThrown()
    }

    def "should enter catch block if objectMapper fails"() {
        given:
        def session = Mock(WebSocketSession)
        def agent = new MockAgentSession("send-email", "team-1", session)
        def failingMapper = Mock(ObjectMapper) {
            writeValueAsString(_) >> { throw new JsonProcessingException("fail") {} }
        }

        registry.find(_ as String, _ as String) >> Optional.of(new ActionRegistry.RegisteredAction(
                "send-email",
                agent,
                null,
                null
        ))
        def dispatcher = new AgentDispatcher(failingMapper, registry)

        def request = new ActionExecutionRequest("ActionExecutionRequest", "cid-1",
                new ActionExecutionRequest.Payload("workflow-1", "send-email", [a: "b"]))

        when:
        def result = dispatcher.dispatch(request, "send-email", "team-1")

        then:
        !result
        agent.sentMessages.isEmpty()
    }
}
