package tech.staffjoy.common.auth;

import org.apache.commons.lang3.StringUtils;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Feign interceptor，for passing auth info to backend
 *
 * @author bobo
 */
public class FeignRequestHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String userId = AuthContext.getUserId();
        if (!StringUtils.isEmpty(userId)) {
            requestTemplate.header(AuthConstant.CURRENT_USER_HEADER, userId);
        }
    }
}