package fo.staffjoy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fo.staffjoy.common.error.GlobalExceptionTranslator;

/**
 * Use this common config for Rest API
 */
@Configuration
@Import(value = { StaffjoyConfig.class, GlobalExceptionTranslator.class })
public class StaffjoyRestConfig {

}
