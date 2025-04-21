package pl.monify.agentgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Import;
import pl.monify.agentgateway.communication.CommunicationConfiguration;

@SpringBootApplication(scanBasePackages = {
        "pl.monify.agentgateway.communication"
}, exclude = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoReactiveAutoConfiguration.class,
        MongoReactiveDataAutoConfiguration.class,
        KafkaAutoConfiguration.class
})
@Import({
        CommunicationConfiguration.class,
        TestMocksConfig.class
})
public class TestApplication {
    static {
        System.setProperty("spring.profiles.active", "test");
    }

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
