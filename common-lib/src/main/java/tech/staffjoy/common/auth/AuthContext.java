package tech.staffjoy.common.auth;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * A context holder class for holding the current userId and authz info
 *
 * @author bobo
 */
public class AuthContext {

    /**
     * get head value from context, return null if no request
     * 
     * @param headerName
     * @return
     */
    private static String getRequetHeader(String headerName) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            String value = request.getHeader(headerName);
            return value;
        }
        return null;
    }

    /**
     * get current user id from request header with key {@link AuthConstant#CURRENT_USER_HEADER} 
     * 
     * @return
     */
    public static String getUserId() {
        return getRequetHeader(AuthConstant.CURRENT_USER_HEADER);
    }

    /**
     * get accessing the internal authorization from request header with key {@link AuthConstant#AUTHORIZATION_HEADER} 
     * 
     * @return
     */
    public static String getAuthz() {
        return getRequetHeader(AuthConstant.AUTHORIZATION_HEADER);
    }

}
