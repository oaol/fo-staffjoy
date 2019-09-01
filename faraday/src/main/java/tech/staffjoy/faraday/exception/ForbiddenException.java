package tech.staffjoy.faraday.exception;

import tech.staffjoy.common.exception.StaffjoyException;

/**
 * 
 * @author bryce
 * @Date Aug 18, 2019
 */
public class ForbiddenException extends StaffjoyException{

    private static final long serialVersionUID = 1L;

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
