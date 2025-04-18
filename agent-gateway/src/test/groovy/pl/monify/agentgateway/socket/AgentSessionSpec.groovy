package pl.monify.agentgateway.socket

import org.springframework.web.reactive.socket.HandshakeInfo
import org.springframework.web.reactive.socket.WebSocketSession
import pl.monify.agentgateway.testutil.MockWebSocketSession
import spock.lang.Specification

class AgentSessionSpec extends Specification {

    def "should initialize AgentSession with defaults"() {
        given:
        def session = Mock(WebSocketSession)
        def agentSession = new AgentSession("agent1", "teamA", session)

        expect:
        agentSession.id == "agent1"
        agentSession.teamId == "teamA"
    }


    def "should prepare message and call send on WebSocket session"() {
        given:
        def msg = '{"type":"ping","correlationId":"abc","payload":null}'
        def handshake = Mock(HandshakeInfo) {
            getUri() >> new URI("ws://localhost/ws/agent?token=ok")
        }
        def session = new MockWebSocketSession(handshake, [msg])
        def agent = new AgentSession("id", "teamX", session)

        when:
        agent.sendText("hello world").block()

        then:
        session.sentMessages == ["hello world"]
    }
}
