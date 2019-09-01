package tech.staffjoy.feign.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    /**
     * 不需要了， 不能强转问题突然解决了。。
     */
//    @Bean
//    public MappingJackson2HttpMessageConverter httpMessageConverter() {
//        MediaType[] mediaTypes = new MediaType[] { MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM,
//                MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN, MediaType.TEXT_XML,
//                MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_PDF, };
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        converter.setSupportedMediaTypes(Arrays.asList(mediaTypes));
//        return converter;
//    }

}
