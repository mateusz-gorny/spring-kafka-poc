package pl.monify.credentialstore;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
@ComponentScan(basePackageClasses = CredentialsStoreModuleConfiguration.class)
public class CredentialsStoreModuleConfiguration {
}
