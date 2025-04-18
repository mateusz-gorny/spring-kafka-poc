package pl.monify.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@ComponentScan(basePackageClasses = UserModuleConfiguration.class)
public class UserModuleConfiguration {
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        List<String> adminAuthorities = List.of(
                "ADMIN",
                "CREDENTIAL_ADMIN",
                "CREDENTIAL_VIEW",
                "TRIGGER_ADMIN",
                "TRIGGER_VIEW",
                "WORKFLOW_ADMIN",
                "WORKFLOW_VIEW"
        );

        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(encoder.encode("rxwEMpmzKWfsx6Sn9vM@*Nqc31QGqPc3")).authorities(adminAuthorities.toArray(new String[] {})).build(),
                User.withUsername("adminact").password(encoder.encode("4UgLz0aU06Zat5h@EvWlWnzXgKFHs8lZ")).authorities(Stream.concat(adminAuthorities.stream(), Stream.of("ACTUATOR"))
                        .toArray(String[]::new)).build(),
                User.withUsername("user").password(encoder.encode("IkAgXQM^Dc6g%8Pgwo!m0q7%pJ9h&I5A")).authorities(
                        "USER",
                        "CREDENTIAL_VIEW",
                        "TRIGGER_VIEW",
                        "WORKFLOW_VIEW"
                ).build()
        );
    }
}
