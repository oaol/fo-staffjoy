package tech.staffjoy.faraday.config;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

import io.micrometer.core.instrument.MeterRegistry;
import tech.staffjoy.common.config.StaffjoyWebConfig;
import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.faraday.core.balancer.LoadBalancer;
import tech.staffjoy.faraday.core.balancer.RandomLoadBalancer;
import tech.staffjoy.faraday.core.filter.FaviconFilter;
import tech.staffjoy.faraday.core.filter.HealthCheckFilter;
import tech.staffjoy.faraday.core.filter.NakedDomainFilter;
import tech.staffjoy.faraday.core.filter.SecurityFilter;
import tech.staffjoy.faraday.core.http.HttpClientProvider;
import tech.staffjoy.faraday.core.http.RequestDataExtractor;
import tech.staffjoy.faraday.core.http.RequestForwarder;
import tech.staffjoy.faraday.core.http.ReverseProxyFilter;
import tech.staffjoy.faraday.core.interceptor.AuthRequestInterceptor;
import tech.staffjoy.faraday.core.interceptor.CacheResponseInterceptor;
import tech.staffjoy.faraday.core.interceptor.PostForwardResponseInterceptor;
import tech.staffjoy.faraday.core.interceptor.PreForwardRequestInterceptor;
import tech.staffjoy.faraday.core.mapping.ConfigurationMappingsProvider;
import tech.staffjoy.faraday.core.mapping.MappingsProvider;
import tech.staffjoy.faraday.core.mapping.MappingsValidator;
import tech.staffjoy.faraday.core.mapping.ProgrammaticMappingsProvider;
import tech.staffjoy.faraday.core.trace.LoggingTraceInterceptor;
import tech.staffjoy.faraday.core.trace.ProxyingTraceInterceptor;
import tech.staffjoy.faraday.core.trace.TraceInterceptor;
import tech.staffjoy.faraday.properties.FaradayProperties;
import tech.staffjoy.faraday.properties.StaffjoyPropreties;
import tech.staffjoy.faraday.view.AssetLoader;

@Configuration
@EnableConfigurationProperties({FaradayProperties.class, StaffjoyPropreties.class})
@Import(value = StaffjoyWebConfig.class)
public class FaradayConfiguration {

    protected final FaradayProperties faradayProperties;
    protected final ServerProperties serverProperties;
    protected final StaffjoyPropreties staffjoyPropreties;
    protected final AssetLoader assetLoader;

    public FaradayConfiguration(FaradayProperties faradayProperties,
                                ServerProperties serverProperties,
                                StaffjoyPropreties staffjoyPropreties,
                                AssetLoader assetLoader) {
        this.faradayProperties = faradayProperties;
        this.serverProperties = serverProperties;
        this.staffjoyPropreties = staffjoyPropreties;
        this.assetLoader = assetLoader;
    }

    @Bean
    public FilterRegistrationBean<ReverseProxyFilter> faradayReverseProxyFilterRegistrationBean(
            ReverseProxyFilter proxyFilter) {
        FilterRegistrationBean<ReverseProxyFilter> registrationBean = new FilterRegistrationBean<>(proxyFilter);
        registrationBean.setOrder(faradayProperties.getFilterOrder()); // by default to Ordered.HIGHEST_PRECEDENCE + 100
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<NakedDomainFilter> nakedDomainFilterRegistrationBean(EnvConfig envConfig) {
        FilterRegistrationBean<NakedDomainFilter> registrationBean =
                new FilterRegistrationBean<>(new NakedDomainFilter(envConfig));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 90); // before ReverseProxyFilter
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<SecurityFilter> securityFilterRegistrationBean(EnvConfig envConfig) {
        FilterRegistrationBean<SecurityFilter> registrationBean =
                new FilterRegistrationBean<>(new SecurityFilter(envConfig));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 80); // before nakedDomainFilter
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<FaviconFilter> faviconFilterRegistrationBean() {
        FilterRegistrationBean<FaviconFilter> registrationBean =
                new FilterRegistrationBean<>(new FaviconFilter(assetLoader.getFaviconFile()));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 75); // before securityFilter
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HealthCheckFilter> healthCheckFilterRegistrationBean() {
        FilterRegistrationBean<HealthCheckFilter> registrationBean =
                new FilterRegistrationBean<>(new HealthCheckFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 70); // before faviconFilter
        return registrationBean;
    }

    @Bean
    @ConditionalOnMissingBean
    public ReverseProxyFilter faradayReverseProxyFilter(
            RequestDataExtractor extractor,
            MappingsProvider mappingsProvider,
            RequestForwarder requestForwarder,
            ProxyingTraceInterceptor traceInterceptor,
            PreForwardRequestInterceptor requestInterceptor
    ) {
        return new ReverseProxyFilter(faradayProperties, extractor, mappingsProvider,
                requestForwarder, traceInterceptor, requestInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClientProvider faradayHttpClientProvider() {
        return new HttpClientProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestDataExtractor faradayRequestDataExtractor() {
        return new RequestDataExtractor();
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingsProvider faradayConfigurationMappingsProvider(EnvConfig envConfig,
                                                    MappingsValidator mappingsValidator,
                                                    HttpClientProvider httpClientProvider) {
        if (faradayProperties.isEnableProgrammaticMapping()) {
            return new ProgrammaticMappingsProvider(
                    envConfig, serverProperties,
                    faradayProperties, mappingsValidator,
                    httpClientProvider);
        } else {
            return new ConfigurationMappingsProvider(
                    serverProperties,
                    faradayProperties, mappingsValidator,
                    httpClientProvider);
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public LoadBalancer faradayLoadBalancer() {
        return new RandomLoadBalancer();
    }

    @Bean
    @ConditionalOnMissingBean
    public MappingsValidator faradayMappingsValidator() {
        return new MappingsValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestForwarder faradayRequestForwarder(
            HttpClientProvider httpClientProvider,
            MappingsProvider mappingsProvider,
            LoadBalancer loadBalancer,
            Optional<MeterRegistry> meterRegistry,
            ProxyingTraceInterceptor traceInterceptor,
            PostForwardResponseInterceptor responseInterceptor
    ) {
        return new RequestForwarder(
                serverProperties, faradayProperties, httpClientProvider,
                mappingsProvider, loadBalancer, meterRegistry,
                traceInterceptor, responseInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceInterceptor faradayTraceInterceptor() {
        return new LoggingTraceInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProxyingTraceInterceptor faradayProxyingTraceInterceptor(TraceInterceptor traceInterceptor) {
        return new ProxyingTraceInterceptor(faradayProperties, traceInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public PreForwardRequestInterceptor faradayPreForwardRequestInterceptor(EnvConfig envConfig) {
        //return new NoOpPreForwardRequestInterceptor();
        return new AuthRequestInterceptor(staffjoyPropreties.getSigningSecret(), envConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public PostForwardResponseInterceptor faradayPostForwardResponseInterceptor() {
        //return new NoOpPostForwardResponseInterceptor();
        return new CacheResponseInterceptor();
    }
}
