package pl.monify.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SendEmailAgent {
    public static void main(String[] args) {
        SpringApplication.run(SendEmailAgent.class, args);
    }
}
