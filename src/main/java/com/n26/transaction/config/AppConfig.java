package com.n26.transaction.config;

import com.n26.transaction.service.Time;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public Time time() {
        return new Time();
    }

}
