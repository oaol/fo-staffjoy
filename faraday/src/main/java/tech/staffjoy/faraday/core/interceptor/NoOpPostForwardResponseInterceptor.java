package tech.staffjoy.faraday.core.interceptor;

import tech.staffjoy.faraday.core.http.ResponseData;
import tech.staffjoy.faraday.properties.MappingProperties;

public class NoOpPostForwardResponseInterceptor implements PostForwardResponseInterceptor {
    @Override
    public void intercept(ResponseData data, MappingProperties mapping) {

    }
}
