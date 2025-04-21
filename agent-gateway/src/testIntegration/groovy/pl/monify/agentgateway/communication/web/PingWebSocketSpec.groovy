package pl.monify.agentgateway.communication.web

import pl.monify.agentgateway.BaseIntegrationSpec

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class PingWebSocketSpec extends BaseIntegrationSpec {

    def "should return pong on valid ping message"() {
        given:
        def token = generateValidJwt(agentId: "agent-001", teamId: "team-abc", action: "ping")
        def latch = new CountDownLatch(1)
        def received = new AtomicReference<String>()

        def ws = connectWs(latch, received, token)

        when:
        ws.send('{"type":"ping"}')
        latch.await(2, TimeUnit.SECONDS)

        then:
        received.get() == '{"type":"pong"}'

        cleanup:
        ws.cancel()
    }
}
