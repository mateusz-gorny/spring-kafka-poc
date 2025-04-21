package pl.monify.agentgateway.token;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Bean;
import pl.monify.agentgateway.token.config.JwtKeysProperties;
import pl.monify.agentgateway.token.adapter.InMemoryJwtKeyProvider;
import pl.monify.agentgateway.token.adapter.JwtKeyLocator;
import pl.monify.agentgateway.token.adapter.JwtTokenGeneratorAdapter;
import pl.monify.agentgateway.token.adapter.JwtTokenParserAdapter;
import pl.monify.agentgateway.token.application.TokenService;
import pl.monify.agentgateway.token.domain.port.in.JwtTokenParserPort;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenGeneratorPort;
import pl.monify.agentgateway.token.domain.port.out.JwtTokenKeyProviderPort;

public class TokenConfiguration {

    @Bean
    public JwtTokenKeyProviderPort jwtTokenKeyProvider(JwtKeysProperties props) {
        return new InMemoryJwtKeyProvider(props.keys());
    }

    @Bean
    public JwtKeyLocator jwtKeyLocator(JwtTokenKeyProviderPort provider) {
        return new JwtKeyLocator(provider);
    }

    @Bean
    public JwtParser jwtParser(JwtKeyLocator locator) {
        return Jwts.parser()
                .keyLocator(locator)
                .build();
    }

    @Bean
    public JwtTokenParserPort jwtTokenParser(JwtParser parser) {
        return new JwtTokenParserAdapter(parser);
    }

    @Bean
    public JwtTokenGeneratorPort jwtTokenGeneratorPort(JwtTokenKeyProviderPort keyProvider, JwtKeysProperties props) {
        return new JwtTokenGeneratorAdapter(keyProvider, props.issuer(), props.tokenTtlSeconds());
    }

    @Bean
    public TokenService tokenService(JwtTokenGeneratorPort tokenGenerator, JwtKeysProperties keysProperties) {
        return new TokenService(tokenGenerator, keysProperties);
    }
}
