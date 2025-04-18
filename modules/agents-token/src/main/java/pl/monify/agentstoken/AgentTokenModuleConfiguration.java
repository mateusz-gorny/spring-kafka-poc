package pl.monify.agentstoken;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories
@EntityScan(basePackageClasses = AgentTokenModuleConfiguration.class)
@ComponentScan(basePackageClasses = AgentTokenModuleConfiguration.class)
public class AgentTokenModuleConfiguration {
}
