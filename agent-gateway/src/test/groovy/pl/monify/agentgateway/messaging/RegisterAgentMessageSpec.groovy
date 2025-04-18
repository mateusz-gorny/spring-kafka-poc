package pl.monify.agentgateway.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class RegisterAgentMessageSpec extends Specification {

    def "should serialize and deserialize RegisterAgentMessage correctly"() {
        given:
        def mapper = new ObjectMapper()
        mapper.findAndRegisterModules()

        def payload = new RegisterAgentMessage.Payload("bar", ["foo":"bar"], ["baz":"qux"])
        def original = new RegisterAgentMessage("register", "corr-123", payload)

        when:
        def json = mapper.writeValueAsString(original)
        def restored = mapper.readValue(json, AgentMessage)

        then:
        restored instanceof RegisterAgentMessage
        restored.type() == "register"
        restored.correlationId() == "corr-123"
        restored.payload().inputSchema["foo"] == "bar"
        restored.payload().outputSchema["baz"] == "qux"
    }
}
