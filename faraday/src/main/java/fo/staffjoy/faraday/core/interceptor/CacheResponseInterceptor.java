package fo.staffjoy.faraday.core.interceptor;

import java.util.List;

import org.springframework.http.HttpHeaders;

import fo.staffjoy.faraday.core.http.ResponseData;
import fo.staffjoy.faraday.properties.MappingProperties;

public class CacheResponseInterceptor implements PostForwardResponseInterceptor {
    @Override
    public void intercept(ResponseData data, MappingProperties mapping) {
        HttpHeaders respHeaders = data.getHeaders();
        if (respHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
            List<String> values = respHeaders.get(HttpHeaders.CONTENT_TYPE);
            if (values.contains("text/html")) {
                // insert header to prevent caching
                respHeaders.set(HttpHeaders.CACHE_CONTROL, "no-cache");
            }
        }
    }
}
