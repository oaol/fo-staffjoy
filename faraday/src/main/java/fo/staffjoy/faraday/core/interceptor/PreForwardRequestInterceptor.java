package fo.staffjoy.faraday.core.interceptor;

import fo.staffjoy.faraday.core.http.RequestData;
import fo.staffjoy.faraday.properties.MappingProperties;

public interface PreForwardRequestInterceptor {
    void intercept(RequestData data, MappingProperties mapping);
}
