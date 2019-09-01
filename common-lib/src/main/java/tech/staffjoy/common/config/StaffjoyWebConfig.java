package tech.staffjoy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Use this common config for Web App
 */
@Configuration
@Import(value = {StaffjoyConfig.class} )
public class StaffjoyWebConfig {
}
