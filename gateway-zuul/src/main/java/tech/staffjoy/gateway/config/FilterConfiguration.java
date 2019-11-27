package tech.staffjoy.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.gateway.filter.PostFilter;
import tech.staffjoy.gateway.filter.SecurityFilter;
import tech.staffjoy.gateway.filter.SimpleFilter;

@Configuration
public class FilterConfiguration {


    @Bean
    public SimpleFilter simpleFilter() {
        return new SimpleFilter();
    }

    @Bean
    public SecurityFilter securityFilter(EnvConfig envConfig) {
        return new SecurityFilter(envConfig);
    }
    
    @Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }
}
