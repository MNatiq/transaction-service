package com.n26.transaction.config;

import com.n26.transaction.service.FakeTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfigTest {

    @Primary
    @Bean
    public FakeTime time() {
        return new FakeTime();
    }

}
