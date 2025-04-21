package system

import pl.monify.agentgateway.BaseIntegrationSpec

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HealthCheckSpec extends BaseIntegrationSpec {

    def "should start server and respond to /actuator/health"() {
        given:
        def client = HttpClient.newHttpClient()
        def request = HttpRequest.newBuilder()
                .uri(baseUri.resolve("/actuator/health"))
                .GET()
                .build()

        when:
        def response = client.send(request, HttpResponse.BodyHandlers.ofString())

        then:
        response.statusCode() == 200
        response.body().contains("UP")
    }
}
