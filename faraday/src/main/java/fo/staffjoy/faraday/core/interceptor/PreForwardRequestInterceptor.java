package fo.staffjoy.faraday.core.interceptor;

import fo.staffjoy.faraday.config.MappingProperties;
import fo.staffjoy.faraday.core.http.RequestData;

public interface PreForwardRequestInterceptor {
    void intercept(RequestData data, MappingProperties mapping);
}
