package fo.staffjoy.faraday.core.interceptor;

import fo.staffjoy.faraday.config.MappingProperties;
import fo.staffjoy.faraday.core.http.RequestData;

public class NoOpPreForwardRequestInterceptor implements PreForwardRequestInterceptor {
    @Override
    public void intercept(RequestData data, MappingProperties mapping) {

    }
}
