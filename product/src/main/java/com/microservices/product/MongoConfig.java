package com.microservices.product;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.microservices.product.converter.OffsetDateTimeReadConverter;
import com.microservices.product.converter.OffsetDateTimeWriteConverter;
import com.microservices.product.repository.ProductRepository;

@Configuration
@EnableMongoRepositories(basePackageClasses = ProductRepository.class)
@EnableMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class MongoConfig {

    @Bean(name = "auditingDateTimeProvider")
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
    
    //Can’t find a codec for class java.time.OffsetDateTime With Mongo DB Spring Boot
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new OffsetDateTimeWriteConverter(),
                new OffsetDateTimeReadConverter()
        ));
    }
}