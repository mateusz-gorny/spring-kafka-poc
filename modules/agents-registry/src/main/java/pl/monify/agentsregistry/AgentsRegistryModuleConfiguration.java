package pl.monify.agentsregistry;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableMongoRepositories
@ComponentScan(basePackageClasses = AgentsRegistryModuleConfiguration.class)
public class AgentsRegistryModuleConfiguration {
}
