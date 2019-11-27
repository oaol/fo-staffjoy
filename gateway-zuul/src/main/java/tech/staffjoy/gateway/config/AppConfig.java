package tech.staffjoy.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import tech.staffjoy.common.config.StaffjoyRestConfig;

@Configuration
@EnableAsync
@Import(value = {StaffjoyRestConfig.class})
public class AppConfig {

}
