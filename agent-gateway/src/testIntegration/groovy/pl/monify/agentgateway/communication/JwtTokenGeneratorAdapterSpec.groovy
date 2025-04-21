package pl.monify.agentgateway.communication


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.LocatorAdapter
import io.jsonwebtoken.ProtectedHeader
import pl.monify.agentgateway.token.adapter.JwtTokenGeneratorAdapter
import pl.monify.agentgateway.token.adapter.JwtTokenParserAdapter
import pl.monify.agentgateway.token.domain.model.JwtAgentClaims
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort
import spock.lang.Specification

import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.security.Key

class JwtTokenGeneratorAdapterSpec extends Specification {

    def "should generate and parse valid JWT with all claims"() {
        given:
        def base64Secret = "VqvZI1B2A7tKeY9PbYz5EUn37K+smnmGaLCE4YjoHkA="
        SecretKey secretKey = new SecretKeySpec(Base64.decoder.decode(base64Secret), "HmacSHA256")

        def keyProvider = Stub(JwtTokenKeyProviderPort) {
            resolveKey("agent-123") >> secretKey
        }

        def generator = new JwtTokenGeneratorAdapter(keyProvider, "monify", 900)

        def parser = Jwts.parser()
                .keyLocator(new LocatorAdapter<Key>() {
                    @Override
                    Key locate(ProtectedHeader header) {
                        return keyProvider.resolveKey(header.getKeyId())
                    }
                })
                .build()

        def tokenParser = new JwtTokenParserAdapter(parser)

        def claims = new JwtAgentClaims(
                "agent-123",
                "team-abc",
                "send-email",
                "agent:ping agent:result",
                "monify",
                List.of("agent-gateway")
        )

        when:
        def token = generator.generate(claims)
        def result = tokenParser.parse(token)

        then:
        result.agentId() == "agent-123"
        result.teamId() == "team-abc"
        result.action() == "send-email"
        result.scope() == "agent:ping agent:result"
        result.issuer() == "monify"
        result.audience() == ["agent-gateway"]
    }
}
