package fo.staffjoy.faraday.core.interceptor;

import fo.staffjoy.faraday.config.MappingProperties;
import fo.staffjoy.faraday.core.http.ResponseData;

public class NoOpPostForwardResponseInterceptor implements PostForwardResponseInterceptor {
    @Override
    public void intercept(ResponseData data, MappingProperties mapping) {

    }
}
