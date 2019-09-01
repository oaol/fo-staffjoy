package tech.staffjoy.faraday.core.interceptor;

import tech.staffjoy.faraday.core.http.RequestData;
import tech.staffjoy.faraday.properties.MappingProperties;

public interface PreForwardRequestInterceptor {
    void intercept(RequestData data, MappingProperties mapping);
}
