package fo.staffjoy.faraday.core.interceptor;

import fo.staffjoy.faraday.config.MappingProperties;
import fo.staffjoy.faraday.core.http.ResponseData;

public interface PostForwardResponseInterceptor {
    void intercept(ResponseData data, MappingProperties mapping);
}
