package pl.monify.agentgateway

import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import pl.monify.agentgateway.token.config.JwtKeysProperties

class TestServerHolder {

    static ConfigurableApplicationContext context
    static URI baseUri
    static int port = 10081
    static Map<String, String> jwtSecrets

    static {
        System.setProperty("server.port", port.toString())
        System.setProperty("spring.profiles.active", "test")

        System.setProperty("JWT_SECRET", "VqvZI1B2A7tKeY9PbYz5EUn37K+smnmGaLCE4YjoHkA=")
        System.setProperty("JWT_DEFAULT_SECRET", "VqvZI1B2A7tKeY9PbYz5EUn37K+smnmGaLCE4YjoHkA=")

        context = SpringApplication.run(TestApplication.class)
        baseUri = new URI("http://localhost:$port")

        jwtSecrets = context.getBean(JwtKeysProperties.class).keys()
    }
}
