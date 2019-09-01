package tech.staffjoy.common.exception;

/**
 * 
 * @author bryce
 * @Date Aug 18, 2019
 */
public class StaffjoyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public StaffjoyException(String message, Throwable cause) {
        super(message, cause);
    }

    public StaffjoyException(String message) {
        super(message);
    }

    /**
     * for better performance
     *
     * @return Throwable
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public Throwable doFillInStackTrace() {
        return super.fillInStackTrace();
    }
}
