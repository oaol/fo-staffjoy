package tech.staffjoy.common.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import tech.staffjoy.common.exception.StaffjoyException;

/**
 * TODO permission http status code
 * 
 * @author bryce
 * @Date Aug 25, 2019
 */
@ResponseStatus(code=HttpStatus.UNAUTHORIZED,reason="Permission Denied")
public class PermissionDeniedException extends StaffjoyException {

    private static final long serialVersionUID = 1L;

    public PermissionDeniedException(String message) {
        super(message);
    }

}
