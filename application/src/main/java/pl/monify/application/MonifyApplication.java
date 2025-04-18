package pl.monify.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
public class MonifyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MonifyApplication.class, args);
    }
}
