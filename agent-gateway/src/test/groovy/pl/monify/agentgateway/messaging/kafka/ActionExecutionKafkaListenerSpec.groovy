package pl.monify.agentgateway.messaging.kafka

import pl.monify.agentgateway.messaging.ActionExecutionRequestMessage
import pl.monify.agentgateway.socket.AgentDispatcher
import spock.lang.Specification

class ActionExecutionKafkaListenerSpec extends Specification {

    def "should dispatch message to agent"() {
        given:
        def dispatcher = Mock(AgentDispatcher)
        def listener = new ActionExecutionKafkaListener(dispatcher)

        def msg = new ActionExecutionRequestMessage(
                "workflow-123",     // workflowInstanceId
                "send-email",       // action
                "team-abc",         // teamId
                "correlation-1",    // correlationId
                [email: "x@x.com"]  // input
        )

        when:
        listener.handle(msg)

        then:
        1 * dispatcher.dispatch(_, "send-email", "team-abc") >> true
    }

    def "should log warning if dispatch returns false"() {
        given:
        def dispatcher = Mock(AgentDispatcher)
        def listener = new ActionExecutionKafkaListener(dispatcher)

        def msg = new ActionExecutionRequestMessage(
                "workflow-123",
                "send-email",
                "team-abc",
                "correlation-1",
                [email: "x@x.com"]
        )

        when:
        listener.handle(msg)

        then:
        1 * dispatcher.dispatch(_, "send-email", "team-abc") >> false
    }
}