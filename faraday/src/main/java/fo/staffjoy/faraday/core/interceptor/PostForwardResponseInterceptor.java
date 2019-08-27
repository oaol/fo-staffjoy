package fo.staffjoy.faraday.core.interceptor;

import fo.staffjoy.faraday.core.http.ResponseData;
import fo.staffjoy.faraday.properties.MappingProperties;

public interface PostForwardResponseInterceptor {
    void intercept(ResponseData data, MappingProperties mapping);
}
