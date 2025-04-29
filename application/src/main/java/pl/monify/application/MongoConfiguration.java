package pl.monify.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;

@Configuration
@EnableMongoRepositories
public class MongoConfiguration {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Collections.emptyList());
    }

    @Bean
    public MongoMappingContext mongoMappingContext(MongoCustomConversions conversions) {
        MongoMappingContext context = new MongoMappingContext();
        context.setSimpleTypeHolder(conversions.getSimpleTypeHolder());
        return context;
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory mongoDatabaseFactory,
            MongoCustomConversions conversions,
            MongoMappingContext context
    ) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        converter.setCustomConversions(conversions);
        converter.setMapKeyDotReplacement("_");
        return converter;
    }
}
