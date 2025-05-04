package pl.monify.agentgateway.communication

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import pl.monify.agentgateway.BaseIntegrationSpec

import javax.crypto.spec.SecretKeySpec
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class JwtValidationSpec extends BaseIntegrationSpec {

    def "should allow WebSocket connection with valid JWT"() {
        given:
        def token = generateValidJwt(agentId: "jwt-valid", teamId: "team-abc", action: "ping")
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

    def "should reject WebSocket connection without JWT"() {
        when:
        def ws = connectWsWithoutToken()

        then:
        thrown(Exception)
        ws?.cancel()
    }

    def "should reject JWT with wrong signature"() {
        given:
        def wrongKey = new SecretKeySpec("wrong-secret-wrong-secret".getBytes(), "HmacSHA256")
        def token = Jwts.builder()
                .claim("team_id", "team-abc")
                .claim("action", "ping")
                .subject("agent-xyz")
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact()

        when:
        def ws = connectWs(new CountDownLatch(1), new AtomicReference<>(), token)

        then:
        thrown(Exception)
        ws?.cancel()
    }

    def "should reject expired JWT"() {
        given:
        def expiredToken = generateValidJwt(agentId: "expired", teamId: "team-abc", action: "ping", expOffset: -60)

        when:
        def ws = connectWs(new CountDownLatch(1), new AtomicReference<>(), expiredToken)

        then:
        thrown(Exception)
        ws?.cancel()
    }

    def "should reject JWT missing required claims"() {
        given:
        def token = Jwts.builder()
                .subject("no-claims")
                .signWith(getDefaultKey(), SignatureAlgorithm.HS256)
                .compact()

        when:
        def ws = connectWs(new CountDownLatch(1), new AtomicReference<>(), token)

        then:
        thrown(Exception)
        ws?.cancel()
    }

    def "should reject JWT with invalid algorithm"() {
        given:
        def token = Jwts.builder()
                .subject("bad-alg")
                .claim("team_id", "team-abc")
                .claim("action", "ping")
                .signWith(getDefaultKey(), SignatureAlgorithm.HS384) // assuming only HS256 is allowed
                .compact()

        when:
        def ws = connectWs(new CountDownLatch(1), new AtomicReference<>(), token)

        then:
        thrown(Exception)
        ws?.cancel()
    }
}
