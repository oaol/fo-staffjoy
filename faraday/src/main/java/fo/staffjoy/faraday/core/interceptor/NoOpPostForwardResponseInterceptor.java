package fo.staffjoy.faraday.core.interceptor;

import fo.staffjoy.faraday.core.http.ResponseData;
import fo.staffjoy.faraday.properties.MappingProperties;

public class NoOpPostForwardResponseInterceptor implements PostForwardResponseInterceptor {
    @Override
    public void intercept(ResponseData data, MappingProperties mapping) {

    }
}
