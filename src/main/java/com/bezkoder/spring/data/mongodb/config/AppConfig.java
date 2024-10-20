package com.bezkoder.spring.data.mongodb.config;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

import java.time.Duration;
import java.util.Map;

@Configuration
public class AppConfig {

    @Bean
    public CloudWatchAsyncClient cloudWatchAsyncClient() {
        return CloudWatchAsyncClient
                .builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    @Bean
    public MeterRegistry getMeterRegistry() {
        CloudWatchConfig cloudWatchConfig = setupCloudWatchConfig();

        MeterRegistry meterRegistry = new CloudWatchMeterRegistry(cloudWatchConfig, Clock.SYSTEM,
                cloudWatchAsyncClient());

        return meterRegistry;
    }

    private CloudWatchConfig setupCloudWatchConfig() {
        CloudWatchConfig cloudWatchConfig = new CloudWatchConfig() {

            private Map<String, String> configuration
                    = Map.of("cloudwatch.namespace", "tutorialsApp",
                    "cloudwatch.step", Duration.ofMinutes(1).toString());

            @Override
            public String get(String key) {
                return configuration.get(key);
            }
        };
        return cloudWatchConfig;
    }
}
