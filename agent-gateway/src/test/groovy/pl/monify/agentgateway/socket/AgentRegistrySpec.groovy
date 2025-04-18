package pl.monify.agentgateway.socket

import org.springframework.web.reactive.socket.WebSocketSession
import pl.monify.agentgateway.testutil.MockAgentSession
import spock.lang.Specification

class AgentRegistrySpec extends Specification {

    def "should return agent if registered and matches action + team"() {
        given:
        def registry = new ActionRegistry()
        def session = Mock(WebSocketSession)
        def agent = new MockAgentSession("send-email", "team-1", session)
        registry.register("team-1", agent.getId(), agent, null, null)

        when:
        def result = registry.find("team-1", "send-email")

        then:
        result.isPresent()
    }

    def "should not return agent if team does not match"() {
        given:
        def registry = new ActionRegistry()
        def session = Mock(WebSocketSession)
        def agent = new MockAgentSession("123", "team-ABC", session)
        registry.register("team-1", agent.getId(), agent, null, null)

        expect:
        registry.find("team-XYZ", "send-email").isEmpty()
    }

    def "should not return agent if action does not match"() {
        given:
        def registry = new ActionRegistry()
        def session = Mock(WebSocketSession)
        def agent = new MockAgentSession("123", "team-1", session)
        registry.register("team-1", agent.getId(), agent, null, null)

        expect:
        registry.find("team-1", "send-email").isEmpty()
    }
}
